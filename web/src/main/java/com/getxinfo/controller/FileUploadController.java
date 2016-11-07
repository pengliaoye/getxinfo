package com.getxinfo.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.drew.imaging.FileType;
import com.drew.imaging.FileTypeDetector;
import com.getxinfo.config.StorageProperties;

@RestController
@RequestMapping("form")
public class FileUploadController {

	public Map<String, Object> options;

	private final Path rootLocation;

	@Autowired
	public FileUploadController(StorageProperties properties) {
		this.rootLocation = Paths.get(properties.getLocation());

		options = new HashMap<>();
		options.put("param_name", "files");
	}

	@GetMapping
	public Map<String, Object> get(HttpServletRequest request) {
		String download = request.getParameter("download");
		String fileName = getFileNameParam(request);
		Map<String, Object> response = null;
		if (StringUtils.isNotBlank(fileName)) {
			response = new HashMap<>();
			response.put(getSingularParamName(), getFileObject(fileName));
		} else {
			response = new HashMap<>();
			String paramName = (String) this.options.get("param_name");
			response.put(paramName, getFileObjects());
		}
		return response;
	}

	@PostMapping
	public Map<String, Object> post(MultipartHttpServletRequest request) {
		String paramName = (String) this.options.get("param_name");
		List<MultipartFile> upload = request.getFiles(paramName);
		String contentDispositionHeader = request.getHeader("Content-Disposition");
		String contentRange = request.getHeader("Content-Range");
		List<Map<String, Object>> files = new ArrayList<>();
		if (upload != null) {
			for (MultipartFile file : upload) {
				Map<String, Object> fileMeta = handleFileUpload(file, file.getName(), file.getSize(),
						file.getContentType());
				files.add(fileMeta);
			}
		}
		Map<String, Object> response = new HashMap<>();
		response.put(paramName, files);
		return response;
	}

	public Map<String, Object> handleFileUpload(MultipartFile file, String name, long size, String type) {
		Map<String, Object> fileMeta = new HashMap<>();
		fileMeta.put("name", "");
		fileMeta.put("size", "");
		fileMeta.put("type", "");
		return fileMeta;
	}

	public String getUploadPath() {
		return rootLocation.toString();
	}

	public String getFullUrl() {
		UriComponents components = UriComponentsBuilder.newInstance().build();
		return components.toString();
	}

	public String getUploadUrl() {
		return getFullUrl() + "/files/";
	}

	public Map<String, Object> getFileObject(String fileName) {
		if (isValidFileObject(fileName)) {
			Map<String, Object> file = new HashMap<>();
			file.put("name", fileName);
			file.put("size", getFileSize(getUploadPath(fileName)));
			file.put("url", getDownloadUrl(fileName));
			return file;
		}
		return null;
	}

	public List<Map<String, Object>> getFileObjects() {
		String uploadDir = getUploadPath();
		File file = new File(uploadDir);
		if (!file.isDirectory()) {
			return Collections.emptyList();
		}

		Path path = Paths.get(uploadDir);
		final List<Map<String, Object>> files = new ArrayList<>();
		try {
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					if (!attrs.isDirectory()) {
						files.add(getFileObject(file.getFileName().toString()));
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return files;
	}

	public String getDownloadUrl(String fileName) {
		return getDownloadUrl(fileName, null);
	}

	public String getDownloadUrl(String fileName, String version) {
		try {
			String versionPath = null;
			if (StringUtils.isBlank(version)) {
				versionPath = "";
			} else {
				Map<String, Object> imageVersions = (Map) this.options.get("image_versions");
				Map<String, Object> versionMap = (Map) imageVersions.get(version);
				String versionUrl = (String) versionMap.get("upload_url");
				if (StringUtils.isNotBlank(versionUrl)) {
					return versionUrl + getUserPath() + URLEncoder.encode(fileName, "UTF-8");
				}
				versionPath = URLEncoder.encode(version, "UTF-8") + "/";
			}
			return getUploadUrl() + getUserPath() + versionPath + URLEncoder.encode(fileName, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean isValidFileObject(String fileName) {
		String filePath = getUploadPath(fileName);
		File file = new File(filePath);
		if (file.exists() && fileName.charAt(0) != '.') {
			return true;
		}
		return false;
	}

	public long getFileSize(String filePath) {
		File file = new File(filePath);
		long length = file.length();
		return length;
	}

	public String trimFileName(String name) {
		name = name.replace("\\", "");
		name = StringUtils.strip(baseName(name), ". ");
		if (StringUtils.isBlank(name)) {
			name = String.valueOf(System.currentTimeMillis());
		}
		return name;
	}

	public String fixFileExtension(String filePath, String name, String type) {
		Pattern pattern = Pattern.compile("^image\\/(gif|jpe?g|png)");
		Matcher matcher = pattern.matcher(type);
		if (name.indexOf(".") < 0 && matcher.matches()) {
			name = name + "." + matcher.group(1);
		}
		
		File file = new File(filePath);
		try {
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream inputStream = new BufferedInputStream(fis);
			FileType fileType = FileTypeDetector.detectFileType(inputStream);
			String extension = null;
			if (fileType == FileType.Jpeg) {
				extension = "jpg";
			} else if (fileType == FileType.Png) {
				extension = "png";
			} else if (fileType == FileType.Gif) {
				
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} 
		return name;
	}

	public String getFileName(String filePath, String name, long size, String type) {
		name = trimFileName(name);

		return name;
	}

	public String getUserPath() {
		return "";
	}

	public String getUploadPath(String fileName) {
		return this.getUploadPath(fileName, null);
	}

	public String getUploadPath(String fileName, String version) {
		fileName = (fileName != null) ? fileName : "";
		String versionPath = null;
		if (StringUtils.isBlank(version)) {
			versionPath = "";
		} else {
			Map<String, Object> imageVersions = (Map) this.options.get("image_versions");
			Map<String, Object> versionMap = (Map) imageVersions.get(version);
			String versionDir = (String) versionMap.get("upload_dir");
			if (StringUtils.isNotBlank(versionDir)) {
				return versionDir + getUserPath() + fileName;
			}
			versionPath = version + "/";
		}
		return getUploadPath() + getUserPath() + versionPath + fileName;
	}

	public String getSingularParamName() {
		String paramName = (String) this.options.get("param_name");
		String name = paramName.substring(0, paramName.length() - 1);
		return name;
	}

	public String getFileNameParam(HttpServletRequest request) {
		String name = this.getSingularParamName();
		String queryParam = request.getParameter(name);
		if (StringUtils.isNotBlank(queryParam)) {
			return baseName(queryParam.replace("\\", ""));
		} else {
			return queryParam;
		}
	}

	public String baseName(String filePath) {
		return this.baseName(filePath, null);
	}

	public String baseName(String filePath, String suffix) {
		filePath = StringUtils.stripEnd(filePath, "/").replaceAll("\\s+$", "");
		String[] splited = filePath.split("/");
		String fileName = "X" + splited[splited.length - 1];
		String extension = FilenameUtils.getExtension(fileName);
		if (("." + extension).equals(suffix)) {
			fileName = FilenameUtils.getBaseName(fileName);
		} else {
			fileName = FilenameUtils.getName(fileName);
		}
		return fileName.substring(1);
	}

	public String handleFormUpload(MultipartHttpServletRequest request, @RequestParam("files") List<Part> parts) {
		for (Part part : parts) {
			String disposition = part.getHeader("content-disposition");
		}
		return "";
	}

}
