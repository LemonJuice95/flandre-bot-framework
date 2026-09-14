package io.lemonjuice.flandre_bot_framework.plugins;

import io.lemonjuice.flandre_bot_framework.FlandreBot;
import lombok.extern.log4j.Log4j2;

import java.util.*;

@Log4j2
public class PluginsLoadingProcessor {
    private final Map<Class<? extends BotPlugin>, BotPlugin> plugins = new HashMap<>();
    private final Set<PluginNode> remainingNodes = new HashSet<>();
    private final Map<PluginNode, String> failedNodes = new HashMap<>();

    public void registerPlugin(BotPlugin plugin) {
        this.plugins.put(plugin.getClass(), plugin);
    }

    public void initConfigs() {
        for(BotPlugin plugin : this.plugins.values()) {
            if(!plugin.initConfig()) {
                FlandreBot.markKeyConfigLost("插件\"" + plugin.getName() + "\"的配置文件需要进行配置");
            }
        }
    }

    public void loadPlugins() {
        this.buildPluginsGraph();
        this.doLoad();

        if(!this.failedNodes.isEmpty()) {
            StringBuilder failedMsg = new StringBuilder();
            failedNodes.forEach((node, reason) -> {
                failedMsg.append(String.format("%-30s (%s)\n", node.plugin.getName(), reason));
            });
            log.warn("以下插件未加载: \n{}", failedMsg.toString().trim());
        }
    }

    private void checkDependenciesVersion(BotPlugin plugin) {
        for(PluginDependency dependency : plugin.getDependencies()) {
            BotPlugin depPlugin = this.plugins.get(dependency.pluginClass());
            if(depPlugin != null) {
                if(!dependency.isVersionValid(depPlugin.getVersion())) {
                    throw new PluginLoadingException(String.format("插件声明的依赖项\"%s\"所需的版本为[%s,%s]，实际提供的版本为%s",
                            depPlugin.getName(),
                            dependency.minVersion(),
                            dependency.maxVersion(),
                            depPlugin.getVersion()
                    ));
                }
            } else {
                //理论上不应到达此处
                throw new PluginLoadingException();
            }
        }
    }

    private void doLoad() {
        ArrayDeque<PluginNode> nextNodes = new ArrayDeque<>();

        for(PluginNode node : this.remainingNodes) {
            if(this.remainingNodes.contains(node) && node.inEdges.isEmpty()) {
                nextNodes.addLast(node);
            }
        }

        while(!nextNodes.isEmpty()) {
            PluginNode node = nextNodes.pollFirst();
            try {
                log.info("正在加载插件: {}", node.plugin.getName());
                this.checkDependenciesVersion(node.plugin);
                node.plugin.load();
                node.outEdges.forEach(n -> {
                    n.inEdges.remove(node);
                    if (this.remainingNodes.contains(n) && n.inEdges.isEmpty()) {
                        nextNodes.addLast(n);
                    }
                });

            } catch (Exception e) {
                log.warn("插件加载失败: {}", node.plugin.getName(), e);
                if (e instanceof PluginLoadingException && e.getMessage() != null && !e.getMessage().isEmpty()) {
                    this.failedNodes.put(node, e.getMessage());
                } else {
                    this.failedNodes.put(node, "自身发生异常");
                }
                this.failSubNodes(node, String.format("依赖项 \"%s\" 加载失败", node.plugin.getName()));
            } finally {
                this.remainingNodes.remove(node);
            }
        }

        this.remainingNodes.forEach(node -> {
            this.failedNodes.put(node, "循环依赖");
        });
    }

    private void buildPluginsGraph() {
        HashMap<Class<? extends BotPlugin>, PluginNode> graph = new HashMap<>();

        PluginNode unknownNode = new PluginNode(new UnknownPlugin());
        graph.put(UnknownPlugin.class, unknownNode);

        this.plugins.forEach((clazz, plugin) -> graph.put(clazz, new PluginNode(plugin)));
        this.plugins.forEach((clazz,plugin) -> {
            PluginNode current = graph.get(clazz);
            try {
                List<PluginDependency> dependencies = plugin.getDependencies();
                for (PluginDependency dependency : dependencies) {
                    Class<? extends BotPlugin> dependencyClass = dependency.pluginClass();
                    if (graph.containsKey(dependencyClass)) {
                        this.addEdge(graph.get(dependencyClass), current);
                    } else {
                        this.addEdge(unknownNode, current);
                        break;
                    }
                }
            } catch (NoClassDefFoundError e) {
                this.addEdge(unknownNode, current);
            }
        });

        this.remainingNodes.addAll(graph.values());
        this.remainingNodes.remove(unknownNode);
        this.failSubNodes(unknownNode, "依赖缺失");
    }

    private void failSubNodes(PluginNode start, String reason) {
        Deque<PluginNode> nodesQueue = new ArrayDeque<>(start.outEdges.stream().filter(this.remainingNodes::contains).toList());
        while (!nodesQueue.isEmpty()) {
            PluginNode current = nodesQueue.peekFirst();
            nodesQueue.pop();
            this.failedNodes.put(current, reason);
            this.remainingNodes.remove(current);
            current.outEdges.stream().filter(this.remainingNodes::contains).forEach(nodesQueue::push);
        }
    }

    private void addEdge(PluginNode from, PluginNode to) {
        from.outEdges.add(to);
        to.inEdges.add(from);
    }

    private static class PluginNode {
        public final BotPlugin plugin;
        public final Set<PluginNode> outEdges = new HashSet<>();
        public final Set<PluginNode> inEdges = new HashSet<>();
        public String cachedReason = "";

        public PluginNode(BotPlugin plugin) {
            this.plugin = plugin;
        }
    }
}
