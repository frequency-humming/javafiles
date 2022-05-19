package data;

public class FileUploadResponse {
	private String fileName;
    private String downloadUri;
    private long size;
 
    // getters and setters are not shown for brevity
    
    FileUploadResponse(){
    	System.out.println("in the constructor of the file upload");
    }
    
    public String getfileName() {
    	return this.fileName;
    }
    
    public String getdownloadUri() {
    	return this.downloadUri;
    }
    
    public long getsize() {
    	return this.size;
    }
    
    public void setFileName(String file) {
    	this.fileName = file;
    }
    public void setSize(Long size) {
    	this.size = size;
    }
    public void setDownloadUri(String Uri) {
    	this.downloadUri = Uri;
    }
}
