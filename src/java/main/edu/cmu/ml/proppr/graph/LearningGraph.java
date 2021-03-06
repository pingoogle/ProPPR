package edu.cmu.ml.proppr.graph;

import java.util.Set;
import java.util.TreeSet;

import edu.cmu.ml.proppr.util.SymbolTable;

public class LearningGraph {
	public final SymbolTable<String> featureLibrary;
	// length = #feature assignments (= sum(edge) #features on that edge)
	public int[] label_feature_id;
	public double[] label_feature_weight;
	
	// length = #edges
	public int[] edge_dest;
	public int[] edge_labels_lo;
	public int[] edge_labels_hi;
	
	// length = #nodes
	public int[] node_near_lo;
	public int[] node_near_hi;
	
	// node_lo = 0;
	public int node_hi;
	
	private int index=0;
	private int labelDependencies=-1;
	
	public LearningGraph(SymbolTable<String> fL) {
		this.featureLibrary = fL;
	}

	
	public Set<String> getFeatureSet() {
		TreeSet<String> features = new TreeSet<String>();
		for (int i : label_feature_id) {
			String f = featureLibrary.getSymbol(i);
			if (!features.contains(f)) features.add(f);
		}
		return features;
	}

	
	public int[] getNodes() {
		int[] nodes = new int[node_hi];
		for (int i=0; i<node_hi; i++) nodes[i] = i;
		return nodes;
	}

	
	public int nodeSize() {
		return node_hi-index;
	}

	
	public int edgeSize() {
		return edge_dest.length;
	}
	
	public void setIndex(int i) {
		this.index = i;
	}
	public void setLabelDependencies(int i) {
		this.labelDependencies=i;
	}
	
	public void serialize(StringBuilder serialized) {
		serialized.append(nodeSize()) // nodes
		.append(LearningGraphBuilder.TAB).append(edgeSize()) //edges
		.append(LearningGraphBuilder.TAB).append(labelDependencySize()) // label dependencies
		.append(LearningGraphBuilder.TAB);
		for (int i = 0; i<getFeatureSet().size(); i++) {
			if (i>0) serialized.append(LearningGraphBuilder.FEATURE_INDEX_DELIM);
			serialized.append(featureLibrary.getSymbol(i+1));
		}
		for (int u=0; u<node_hi; u++) {
			for (int ec=node_near_lo[u]; ec<node_near_hi[u]; ec++) {
				int v = edge_dest[ec];
				serialized.append(LearningGraphBuilder.TAB)
				.append(u)
				.append(LearningGraphBuilder.SRC_DST_DELIM)
				.append(v).append(LearningGraphBuilder.EDGE_DELIM);
				for (int lc = edge_labels_lo[ec]; lc < edge_labels_hi[ec]; lc++) {
					if (lc > edge_labels_lo[ec]) serialized.append(LearningGraphBuilder.EDGE_FEATURE_DELIM);
					serialized.append(label_feature_id[lc]).append(LearningGraphBuilder.FEATURE_WEIGHT_DELIM).append(label_feature_weight[lc]);
				}
			}
		}
	}

	
	public int labelDependencySize() {
		return this.labelDependencies;
	}
}
