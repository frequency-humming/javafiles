package data;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;

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
	    uploadImage(filecode);
	    FileUploadResponse response = new FileUploadResponse();
	    response.setFileName(fileName);
	    response.setSize(size);
	    response.setDownloadUri("/downloadFile/" + filecode);
	    
	    return new ResponseEntity<>(response, HttpStatus.OK); 
	}
	@PostMapping("/htmltoPDF")
	public void transformPDF() throws IOException {
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
	
}
