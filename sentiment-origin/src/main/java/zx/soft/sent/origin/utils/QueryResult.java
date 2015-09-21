package zx.soft.sent.origin.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.response.GroupResponse;
import org.apache.solr.client.solrj.response.PivotField;
import org.apache.solr.client.solrj.response.RangeFacet;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;

/**
 * 查询结果类
 *
 * @author wanggang
 *
 */
@SuppressWarnings("rawtypes")
public class QueryResult {

	// 多线程的标志 added by donglei
	private String tag;

	private long numFound;
	// Direct pointers to known types
	private int QTime;
	private NamedList<Object> header = null;
	private SolrDocumentList results = new SolrDocumentList();
	private NamedList<ArrayList> sort = null;

	private Map<String, Map<String, List<String>>> highlighting = null;
	private GroupResponse group = null;

	// Facet stuff
	private Map<String, Integer> facetQuery = null;
	private List<SimpleFacetInfo> facetFields = null;
	private List<SimpleFacetInfo> facetDates = null;
	private List<RangeFacet> facetRanges = null;
	private NamedList<List<PivotField>> facetPivot = null;

	public int getQTime() {
		return QTime;
	}

	public void setQTime(int qTime) {
		QTime = qTime;
	}

	public long getNumFound() {
		return numFound;
	}

	public void setNumFound(long numFound) {
		this.numFound = numFound;
	}

	public NamedList<Object> getHeader() {
		return header;
	}

	public void setHeader(NamedList<Object> header) {
		this.header = header;
	}

	public SolrDocumentList getResults() {
		return results;
	}

	public void setResults(SolrDocumentList results) {
		this.results = results;
	}

	public NamedList<ArrayList> getSort() {
		return sort;
	}

	public void setSort(NamedList<ArrayList> sort) {
		this.sort = sort;
	}

	public Map<String, Map<String, List<String>>> getHighlighting() {
		return highlighting;
	}

	public void setHighlighting(Map<String, Map<String, List<String>>> highlighting) {
		this.highlighting = highlighting;
	}

	public GroupResponse getGroup() {
		return group;
	}

	public void setGroup(GroupResponse group) {
		this.group = group;
	}

	public Map<String, Integer> getFacetQuery() {
		return facetQuery;
	}

	public void setFacetQuery(Map<String, Integer> facetQuery) {
		this.facetQuery = facetQuery;
	}

	public List<SimpleFacetInfo> getFacetFields() {
		return facetFields;
	}

	public void setFacetFields(List<SimpleFacetInfo> facetFields) {
		this.facetFields = facetFields;
	}

	public List<SimpleFacetInfo> getFacetDates() {
		return facetDates;
	}

	public void setFacetDates(List<SimpleFacetInfo> facetDates) {
		this.facetDates = facetDates;
	}

	public List<RangeFacet> getFacetRanges() {
		return facetRanges;
	}

	public void setFacetRanges(List<RangeFacet> facetRanges) {
		this.facetRanges = facetRanges;
	}

	public NamedList<List<PivotField>> getFacetPivot() {
		return facetPivot;
	}

	public void setFacetPivot(NamedList<List<PivotField>> facetPivot) {
		this.facetPivot = facetPivot;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

}
