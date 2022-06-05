package data;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
 
@RestController
public class FileUploadController {

	@PostMapping("/uploadFile")
	public ResponseEntity<FileUploadResponse> uploadFile(
		@RequestParam("file") MultipartFile multipartFile) throws IOException {
	    
	    String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
	    System.out.println(fileName);
	    long size = multipartFile.getSize();
	    System.out.println(size);
	    String filecode = FileUploadUtil.saveFile(fileName, multipartFile);
	    //call the PDF to image method
	    uploadImage(filecode);
	    FileUploadResponse response = new FileUploadResponse();
	    response.setFileName(fileName);
	    response.setSize(size);
	    response.setDownloadUri("/downloadFile/" + filecode);
	    
	    return new ResponseEntity<>(response, HttpStatus.OK); 
	}
	@PostMapping("/htmltoPDF")
	public String transformPDF() throws IOException {
		String htmltext = "<h1>hello</h1><h2>over here</h2>";
		try {
			Document document = new Document();
			PdfWriter writer = PdfWriter.getInstance(document,
					new FileOutputStream("output/html.pdf"));
			StringReader Html = new StringReader(htmltext);
			document.open();
			//XMLWorkerHelper.getInstance().parseXHtml(writer, document,new FileInputStream("index.html"));
			XMLWorkerHelper.getInstance().parseXHtml(writer, document,Html);
			document.close();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("error"+e);
		}
		return htmltext; 
	}
	@PostMapping("/html")
	public void transformPDF(@RequestBody String request) throws IOException,DocumentException{
		System.out.println(request);
		try {
			Document document = new Document();
			PdfWriter writer = PdfWriter.getInstance(document,
					new FileOutputStream("output/html.pdf"));
			StringReader Html = new StringReader(request);
			document.open();
			//XMLWorkerHelper.getInstance().parseXHtml(writer, document,new FileInputStream("index.html"));
			XMLWorkerHelper.getInstance().parseXHtml(writer, document,Html);
			document.close();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("error"+e);
		}   
	}
	
	public void uploadImage(String filecode) throws IOException {
		
	    File file = new File("Files-Upload/"+filecode);
	    PDDocument document = PDDocument.load(file);
	    PDFRenderer pdfRenderer = new PDFRenderer(document);
	    for(int page = 0; page < document.getNumberOfPages(); ++page) {
	    	BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
	    	ImageIOUtil.writeImage(bim, String.format("output/pdf-%d.%s", page + 1, ".jpg"), 300);
	    }
	    document.close();
	}
	
	@PostMapping("/imagetopdf")
	public String ImageToPdf(@RequestParam("file") MultipartFile multipartfile) throws IOException, DocumentException  {
		//System.out.println(PageSize.A4.getWidth());
		long size = multipartfile.getSize();
		int maxsize = 7340032;
		
		Path uploadPath = Paths.get("Files-Upload");
		System.out.println("path is :"+uploadPath);
		String fileName = StringUtils.cleanPath(multipartfile.getOriginalFilename());
		System.out.println("filename is :"+fileName);
		
		if(size > maxsize) {
			return "file must be smaller that 7mb";
		}
		try (InputStream inputStream = multipartfile.getInputStream()) {
	        Path filePath = uploadPath.resolve(fileName);
	        Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
	            
			System.out.println("in the try method");
			Image img = Image.getInstance("Files-Upload/"+fileName);
			img.scaleToFit(400, 400);
			img.setAlignment(Element.ALIGN_CENTER);
			Document document = new Document();
			String output = "output/images/transforming.pdf";
			FileOutputStream fos = new FileOutputStream(output);
			PdfWriter writer = PdfWriter.getInstance(document, fos);
			System.out.println("past the pdf writer");
			writer.open();
			document.open();
			document.add(img);
			document.close();
			writer.close();
		} catch (IOException | DocumentException e) {
			System.out.println("In the exception "+e);
		}
		return "Image Converted Successfully";
			
	}
	
}
