package cn.sm1234.controller;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UploadController {
	
	Map<String,Object> result = new HashMap<String,Object>();
	/*
	 * 接收文件
	 */
	@RequestMapping("/uploadAttach")
	public Map<String,Object> upload(@RequestParam("attach")MultipartFile file) throws Exception{
		//处理文件
		System.out.println("文件原名称："+file.getOriginalFilename());
		System.out.println("文件类型："+file.getContentType());
		
		//保存到硬盘
		file.transferTo(new File("e:/"+file.getOriginalFilename()));
		
		result.put("success", true);
		return result;
	} 
}
