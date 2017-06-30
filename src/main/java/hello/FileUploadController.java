package hello;

import hello.storage.StorageFileNotFoundException;
import hello.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class FileUploadController{
	
	private final StorageService storageService;
	private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);
	
	@Autowired
	public FileUploadController(StorageService storageService){
		this.storageService = storageService;
	}
	
	@GetMapping("/")
	public String listUploadFiles(Model model) throws IOException{
		model.addAttribute("files", storageService
		.loadAll()
		.map(path -> 
				MvcUriComponentsBuilder
							.fromMethodName(FileUploadController.class, "serveFile", path.getFileName().toString())
							.build().toString())
							.collect(Collectors.toList()));
		return "uploadForm";
	}
	
	@GetMapping("/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename){
		Resource file = storageService.loadAsResource(filename);
		return ResponseEntity
			.ok()
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+file.getFilename()+"\"")
			.body(file);	
	}
	
	/**
	 * Upload file using Spring Controller
	 */
	@PostMapping("/")
	public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes){
		if (!file.isEmpty()) {
			try {
				byte[] bytes = file.getBytes();
				
				// Creating a directory in current file path to store uploaded file
				File directory = new File(".");
				File dir = new File(directory.getCanonicalPath() + File.separator + "tmpUploadFile");
				if (!dir.exists())
					dir.mkdirs();
				
				//Move the uploaded file to the temporary directory
				String fullFilePath = dir.getAbsolutePath() + File.separator + file.getOriginalFilename();
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(fullFilePath));
				stream.write(bytes);
				stream.close();
				
				//Allows user to download file
				storageService.store(file); 
				
				redirectAttributes.addFlashAttribute("message","You successfully uploaded " + fullFilePath + "!");
				logger.info("File successfully loaded to: " + fullFilePath);
				
				return "redirect:/";
			} catch (Exception e) {
				logger.info("You failed to upload  => " + e.getMessage());
				return "You failed to upload  => " + e.getMessage();
			}
		} else {
			logger.info("You failed to upload  because the file was empty.");
			return "You failed to upload  because the file was empty.";
		}
	}
	
	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity handleStorageFileNotFound(StorageFileNotFoundException exc){
		return ResponseEntity.notFound().build();
	}
}