package test.github.richardwilly98.esdms.rest;

import static org.elasticsearch.common.io.Streams.copyToBytesFromClasspath;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

//import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.esdms.api.Document;
import com.github.richardwilly98.esdms.api.Version;
import com.github.richardwilly98.esdms.rest.RestDocumentService;
import com.github.richardwilly98.esdms.rest.RestServiceBase;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
//import com.sun.jersey.multipart.FormDataParam;

public class TestRestDocumentService extends GuiceAndJettyTestBase<Document> {
	// public class TestRestDocumentService extends
	// GuiceAndJerseyTestBase<Document> {

	public TestRestDocumentService() throws Exception {
		super();
	}

	@Test
	public void testCreateDeleteDocument() throws Throwable {
		log.debug("*** testCreateDeleteDocument ***");
		try {
			String name = "test-attachment.html";
			Document document = createDocument(name, "text/html",
					"/test/github/richardwilly98/services/test-attachment.html");
			Assert.assertNotNull(document);
			Assert.assertNotNull(document.getCurrentVersion());
			log.debug("New document: " + document);
			Assert.assertEquals(document.getName(), name);
			Document document2 = get(document.getId(), Document.class,
					RestDocumentService.DOCUMENTS_PATH);
			Assert.assertEquals(document.getId(), document.getId());
			String newName = "document-" + System.currentTimeMillis();
			document2.setName(newName);
			Document document3 = update(document2, Document.class,
					RestDocumentService.DOCUMENTS_PATH);
			Assert.assertEquals(newName, document3.getName());
			markDeletedDocument(document.getId());
			delete(document.getId(), RestDocumentService.DOCUMENTS_PATH);
			document2 = get(document.getId(), Document.class,
					RestDocumentService.DOCUMENTS_PATH);
			Assert.assertNull(document2);
		} catch (Throwable t) {
			log.error("testCreateDeleteDocument fail", t);
			Assert.fail();
		}
	}

	@Test
	public void testCheckoutCheckinDocument() throws Throwable {
		log.debug("*** testCheckoutCheckinDocument ***");
		try {
			String name = "test-attachment.html";
			Document document = createDocument(name, "text/html",
					"/test/github/richardwilly98/services/test-attachment.html");
			Assert.assertNotNull(document);
			log.debug("New document: " + document);

			ClientResponse response = resource()
					.path(RestDocumentService.DOCUMENTS_PATH)
					.path(document.getId()).path("checkout")
					.cookie(adminCookie).type(MediaType.APPLICATION_JSON)
					.post(ClientResponse.class);
			log.debug(String.format("status: %s", response.getStatus()));
			Assert.assertTrue(response.getStatus() == Status.NO_CONTENT
					.getStatusCode());

			Document document2 = get(document.getId(), Document.class,
					RestDocumentService.DOCUMENTS_PATH);
			log.debug("Checked-out document: " + document);
			Map<String, Object> attributes = document2.getAttributes();
			Assert.assertNotNull(attributes.get(Document.STATUS));
			Assert.assertTrue(attributes.get(Document.STATUS).equals(
					Document.DocumentStatus.LOCKED.getStatusCode()));
			Assert.assertNotNull(attributes.get(Document.LOCKED_BY));
			Assert.assertTrue(attributes.get(Document.LOCKED_BY).equals(
					adminCredential.getUsername()));
			Assert.assertNotNull(attributes.get(Document.MODIFIED_DATE));

			response = resource().path(RestDocumentService.DOCUMENTS_PATH)
					.path(document.getId()).path("checkout")
					.cookie(adminCookie).type(MediaType.APPLICATION_JSON)
					.post(ClientResponse.class);
			log.debug(String.format("status: %s", response.getStatus()));
			Assert.assertTrue(response.getStatus() == Status.CONFLICT
					.getStatusCode());

			response = resource().path(RestDocumentService.DOCUMENTS_PATH)
					.path(document.getId()).path("checkin").cookie(adminCookie)
					.type(MediaType.APPLICATION_JSON)
					.post(ClientResponse.class);
			log.debug(String.format("status: %s", response.getStatus()));
			Assert.assertTrue(response.getStatus() == Status.NO_CONTENT
					.getStatusCode());

			document2 = get(document.getId(), Document.class,
					RestDocumentService.DOCUMENTS_PATH);

			attributes = document2.getAttributes();
			Assert.assertTrue(document2.getAttributes().get(Document.STATUS)
					.equals(Document.DocumentStatus.AVAILABLE.getStatusCode()));
			Assert.assertFalse(attributes.containsKey(Document.LOCKED_BY));

			response = resource().path(RestDocumentService.DOCUMENTS_PATH)
					.path(document.getId()).path("checkin").cookie(adminCookie)
					.type(MediaType.APPLICATION_JSON)
					.post(ClientResponse.class);
			log.debug(String.format("status: %s", response.getStatus()));
			Assert.assertTrue(response.getStatus() == Status.CONFLICT
					.getStatusCode());

		} catch (Throwable t) {
			log.error("testCheckoutCheckinDocument fail", t);
			Assert.fail();
		}
	}

	@Test()
	public void testDownloadDocument() throws Throwable {
		log.debug("*** testDownloadDocument ***");
		try {
			String name = "test-attachment.html";
			Document document = createDocument(name, "text/html",
					"/test/github/richardwilly98/services/test-attachment.html");
			Assert.assertNotNull(document);
			log.debug("New document: " + document);

			ClientResponse response = resource()
					.path(RestDocumentService.DOCUMENTS_PATH)
					.path(document.getId()).path("download")
					.cookie(adminCookie)
					// .type(MediaType.APPLICATION_JSON)
					.get(ClientResponse.class);
			log.debug(String.format("status: %s", response.getStatus()));
			Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());

			log.debug("Content type: " + response.getType().getType());
			InputStream stream = response.getEntityInputStream();
			Assert.assertNotNull(stream);
		} catch (Throwable t) {
			log.error("testDownloadDocument fail", t);
			Assert.fail();
		}
	}

	@Test
	public void testFindDocuments() throws Throwable {
		log.debug("*** testFindDocuments ***");
		try {
			String criteria = "Aliquam";
			Document document = createDocument("test-attachment.html",
					"text/html",
					"/test/github/richardwilly98/services/test-attachment.html");
			Assert.assertNotNull(document);
			ClientResponse response = resource()
					.path(RestDocumentService.DOCUMENTS_PATH)
					.path(RestServiceBase.SEARCH_PATH).path(criteria)
					.cookie(adminCookie).accept(MediaType.APPLICATION_JSON)
					.get(ClientResponse.class);
			log.debug(String.format("status: %s", response.getStatus()));
			Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
			List<Document> documents = response
					.getEntity(new GenericType<List<Document>>() {
					});
			Assert.assertNotNull(documents);
			Assert.assertTrue(documents.size() >= 1);
		} catch (Throwable t) {
			log.error("testFindDocuments fail", t);
			Assert.fail();
		}

	}
	
	@Test
	public void testCreateDocumentVersions() throws Throwable {
		log.debug("*** testCreateDocumentVersions ***");
		try {
			String name = "Aliquam";
			String criteria  = "Aliquam";
			
			Document document = createDocument("test-attachment.html",
					"text/html",
					"/test/github/richardwilly98/services/test-attachment.html");
			Assert.assertNotNull(document);
			String contentType = "text/plain";
			Version oldV = document.getCurrentVersion();
			log.debug(String.format("step 1 obtained document %s having %s versions. Current version %s", 
									document.getId(), document.getVersions().size(), oldV.getVersionId()));
			Assert.assertEquals(oldV.getVersionId(), 1);
			Version newV = createVersion(document.getId(), name, contentType, "/test/github/richardwilly98/services/test-attachment.html");			
			log.debug(String.format("step 2 obtained document %s having %s versions. Current version %s, New version %s", 
					document.getId(), document.getVersions().size(), document.getCurrentVersion().getVersionId(), newV.getVersionId()));
			Assert.assertEquals(newV.getVersionId(), 2);
			
			newV = createFromVersion(document.getId(), "" + oldV.getVersionId(), name, contentType, "/test/github/richardwilly98/services/test-attachment.html");			
			log.debug(String.format("step 3 obtained document %s having %s versions. Current version %s, New version %s", 
					document.getId(), document.getVersions().size(), document.getCurrentVersion().getVersionId(), newV.getVersionId()));
			Assert.assertEquals(newV.getVersionId(), 3);
			
			ClientResponse response = resource()
					.path(RestDocumentService.DOCUMENTS_PATH)
					.path(RestServiceBase.SEARCH_PATH).path(criteria)
					.cookie(adminCookie).accept(MediaType.APPLICATION_JSON)
					.get(ClientResponse.class);
			log.debug(String.format("status: %s", response.getStatus()));
			Assert.assertTrue(response.getStatus() == Status.OK.getStatusCode());
			List<Document> documents = response
					.getEntity(new GenericType<List<Document>>() {
					});
			Assert.assertNotNull(documents);
			Assert.assertTrue(documents.size() >= 1);
		} catch (Throwable t) {
			log.error("testCreateDocumentVersions fail", t);
			Assert.fail();
		}
		log.debug("*** testCreateDocumentVersions end ***");
	}
	
	private Version createVersion(String documentId, String name, String contentType, String path)
			throws Throwable {
		log.debug("******* createVersion  *********");
		byte[] content = copyToBytesFromClasspath(path);
		InputStream is = new ByteArrayInputStream(content);
		
		FormDataMultiPart form = new FormDataMultiPart();
		form.field("name", name);
		
		FormDataBodyPart p = new FormDataBodyPart("file", is,
				MediaType.valueOf(contentType));
		form.bodyPart(p);

		ClientResponse response = resource()
				.path(RestDocumentService.DOCUMENTS_PATH).path(documentId)
				.path(RestDocumentService.UPLOAD_PATH).cookie(adminCookie)
				.type(MediaType.MULTIPART_FORM_DATA)
				.post(ClientResponse.class, form);
		log.debug(String.format("createVersion clientResponse location: %s", response.getLocation()));
		log.debug(String.format("createVersion clientResponse cookie: %s", response.getCookies()));
		log.debug(String.format("createVersion clientResponse toString: %s", response.toString()));
		log.debug(String.format("createVersion clientResponse status: %s", response.getStatus()));
		Assert.assertTrue(response.getStatus() == Status.CREATED
				.getStatusCode());
		URI uri = response.getLocation();
		Assert.assertNotNull(uri);
		log.debug("******* createVersion end *********");
		return get(uri, Document.class).getCurrentVersion();
	}
	
	private Version createFromVersion(String documentId, String versionId, String name, String contentType, String path)
			throws Throwable {
		log.debug("******* createFromVersion  *********");
		byte[] content = copyToBytesFromClasspath(path);
		InputStream is = new ByteArrayInputStream(content);
		
		FormDataMultiPart form = new FormDataMultiPart();
		form.field("name", name);
		
		FormDataBodyPart p = new FormDataBodyPart("file", is,
				MediaType.valueOf(contentType));
		form.bodyPart(p);

		ClientResponse response = resource()
				.path(RestDocumentService.DOCUMENTS_PATH).path(documentId)
				.path(versionId)
				.path(RestDocumentService.UPLOAD_PATH).cookie(adminCookie)
				.type(MediaType.MULTIPART_FORM_DATA)
				.post(ClientResponse.class, form);
		log.debug(String.format("createFromVersion clientResponse location: %s", response.getLocation()));
		log.debug(String.format("createFromVersion clientResponse cookie: %s", response.getCookies()));
		log.debug(String.format("createFromVersion clientResponse toString: %s", response.toString()));
		log.debug(String.format("createFromVersion clientResponse status: %s", response.getStatus()));
		Assert.assertTrue(response.getStatus() == Status.CREATED
				.getStatusCode());
		URI uri = response.getLocation();
		Assert.assertNotNull(uri);
		log.debug("******* createFromVersion end *********");
		return get(uri, Document.class).getCurrentVersion();
	}

	private Document createDocument(String name, String contentType, String path)
			throws Throwable {
		String id = String.valueOf(System.currentTimeMillis());
		byte[] content = copyToBytesFromClasspath(path);
		InputStream is = new ByteArrayInputStream(content);

		// Filename of the sent stream is not relevant for this test.
		// StreamDataBodyPart streamData = new StreamDataBodyPart("file", is,
		// name);
		FormDataMultiPart form = new FormDataMultiPart();
		form.field("name", name);
		// FormDataBodyPart p = new FormDataBodyPart(FormDataContentDisposition
		// .name("name").build(), name);
		// p.contentDisposition(ContentDisposition.type(contentType).fileName(name).size(content.length).build());
		// mp.bodyPart(p);
		FormDataBodyPart p = new FormDataBodyPart("file", is,
				MediaType.valueOf(contentType));
		form.bodyPart(p);
		// mp.bodyPart(streamData);

		ClientResponse response = resource()
				.path(RestDocumentService.DOCUMENTS_PATH)
				.path(RestDocumentService.UPLOAD_PATH).cookie(adminCookie)
				.type(MediaType.MULTIPART_FORM_DATA)
				.post(ClientResponse.class, form);
		log.debug(String.format("status: %s", response.getStatus()));
		Assert.assertTrue(response.getStatus() == Status.CREATED
				.getStatusCode());
		URI uri = response.getLocation();
		Assert.assertNotNull(uri);
		return get(uri, Document.class);
	}

	private void markDeletedDocument(String id)
			throws Throwable {
		ClientResponse response = resource().path(RestDocumentService.DOCUMENTS_PATH).path(id).path(RestDocumentService.MARKDELETED_PATH)
				.cookie(adminCookie).type(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class);
		log.debug(String.format("status: %s", response.getStatus()));
		Assert.assertTrue(response.getStatus() == Status.NO_CONTENT.getStatusCode());
	}
}
