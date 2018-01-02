package cn.e3mall.test;

import org.junit.Test;

import cn.e3mall.common.utils.FastDFSClient;

public class FastDFSClientTest {
	
	@Test
	public void tset() throws Exception{
		FastDFSClient dfcClient=new FastDFSClient("D:/javaExercise/e3mall/e3-manager-web/src/main/resources/conf/tracker.conf");
		String uploadFile = dfcClient.uploadFile("C:/Users/64566/Desktop/$4P($C0$E2XI57U%@Z$GB1C.jpg");
		System.out.println(uploadFile);
	}
}
