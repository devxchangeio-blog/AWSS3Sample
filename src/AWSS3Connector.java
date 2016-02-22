/**
 * 
 */

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;

/**
 * @author karthy
 * 
 */
public class AWSS3Connector {

	private static final String SUFFIX = "/";

	public static void main(String[] args) {
		// credentials object identifying user for authentication
		// user must have AWSConnector and AmazonS3FullAccess for
		// this example to work
		AWSCredentials credentials = new BasicAWSCredentials("AKIAI2DZKUFFM4ONFOLA",
				"+RzKOI+7cP3JXfQIuUNSrGJPx+Sq+JstdSFv85CA");

		// create a client connection based on credentials
		AmazonS3 s3client = new AmazonS3Client(credentials);

		// create bucket - name must be unique for all S3 users
		String bucketName = "oneclicklabsbucket";
		s3client.createBucket(bucketName);

		// list buckets
		for (Bucket bucket : s3client.listBuckets()) {
			System.out.println(" - " + bucket.getName());
		}

		// create folder into bucket
		String folderName = "testfolder";
		createFolder(bucketName, folderName, s3client);

		// upload file to folder and set it to public
		String fileName = folderName + SUFFIX + "site_1.xml";
		s3client.putObject(new PutObjectRequest(bucketName, fileName, new File("\\Users\\karthy\\Downloads\\site_1.xml"))
				.withCannedAcl(CannedAccessControlList.PublicRead));

		deleteFolder(bucketName, folderName, s3client);

		// deletes bucket
		s3client.deleteBucket(bucketName);
	}

	public static void createFolder(String bucketName, String folderName,
			AmazonS3 client) {
		// create meta-data for your folder and set content-length to 0
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(0);

		// create empty content
		InputStream emptyContent = new ByteArrayInputStream(new byte[0]);

		// create a PutObjectRequest passing the folder name suffixed by /
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,
				folderName + SUFFIX, emptyContent, metadata);

		// send request to S3 to create folder
		client.putObject(putObjectRequest);
	}

	/**
	 * This method first deletes all the files in given folder and than the
	 * folder itself
	 */
	public static void deleteFolder(String bucketName, String folderName,
			AmazonS3 client) {
		List<S3ObjectSummary> fileList = client.listObjects(bucketName, folderName)
				.getObjectSummaries();
		for (S3ObjectSummary file : fileList) {
			client.deleteObject(bucketName, file.getKey());
		}
		client.deleteObject(bucketName, folderName);
	}
}
