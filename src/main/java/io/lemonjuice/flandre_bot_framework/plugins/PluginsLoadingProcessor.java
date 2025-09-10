package io.lemonjuice.flandre_bot_framework.plugins;

import lombok.extern.log4j.Log4j2;

import java.util.*;

@Log4j2
public class PluginsLoadingProcessor {
    private final List<BotPlugin> plugins = new ArrayList<>();
    private final Set<PluginNode> remainingNodes = new HashSet<>();
    private final Map<PluginNode, String> failedNodes = new HashMap<>();

    public void registerPlugin(BotPlugin plugin) {
        this.plugins.add(plugin);
    }

    public void loadPlugins() {
        this.buildPluginsGraph();
        this.doLoad();

        if(!this.failedNodes.isEmpty()) {
            StringBuilder failedMsg = new StringBuilder();
            failedNodes.forEach((node, reason) -> {
                failedMsg.append(String.format("%s (%s)\n", node.plugin.getName(), reason));
            });
            log.warn("以下插件未加载: \n{}", failedMsg.toString().trim());
        }
    }

    private void doLoad() {
        boolean completedFlag = false;
        while(!completedFlag) {
            completedFlag = true;
            for(PluginNode node : this.remainingNodes) {
                if(node.inEdges.isEmpty()) {
                    completedFlag = false;
                    try {
                        log.info("正在加载插件: {}", node.plugin.getName());
                        node.plugin.load();
                        node.outEdges.forEach(n -> n.inEdges.remove(node));
                    } catch (Exception e) {
                        this.failedNodes.put(node, "自身发生异常");
                        this.failSubNodes(node, "依赖项加载失败");
                    } finally {
                        this.remainingNodes.remove(node);
                    }
                }
            }
        }

        this.remainingNodes.forEach(node -> {
            this.failedNodes.put(node, "循环依赖");
            this.remainingNodes.remove(node);
        });
    }

    private void buildPluginsGraph() {
        HashMap<Class<? extends BotPlugin>, PluginNode> graph = new HashMap<>();

        PluginNode unknownNode = new PluginNode(new UnknownPlugin());
        graph.put(UnknownPlugin.class, unknownNode);

        this.plugins.forEach(plugin -> graph.put(plugin.getClass(), new PluginNode(plugin)));
        this.plugins.forEach(plugin -> {
            PluginNode current = graph.get(plugin.getClass());
            try {
                List<Class<? extends BotPlugin>> dependencies = plugin.getDependencies();
                for (Class<? extends BotPlugin> clazz : dependencies) {
                    if (graph.containsKey(clazz)) {
                        this.addEdge(graph.get(clazz), current);
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

        public PluginNode(BotPlugin plugin) {
            this.plugin = plugin;
        }
    }
}
