package com.autotiming.csck.netapi;

public class URIConstants {
	public static final String BASE_URI = "http://news.hiho.autotiming.com/rest/v1/";
	
	/**
	 * 视频碎片
	 * @author qiuli
	 */
	public static class FragmnetIndex {
		public static final String PATH = "fragment/indexV2";
		public static final String PARAMETER_PAGE = "page";
		public static final String PARAMETER_ORDERBY = "order_by";
		public static final String PARAMETER_LIMIT = "limit";
		public static final String FULL_PATH = BASE_URI+PATH;
    }
}
