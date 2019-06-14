package com.k9b9.springjwtsimple;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AppTests {

	private static String API_AUTH_AUTHENTICATE = "/api/auth/authenticate";
	private static String API_TEST_ADMIN = "/api/test/admin";
	private static String API_TEST_USER = "/api/test/user";
	private static String API_TEST_CUSTOMER = "/api/test/customer";

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	ObjectMapper mapper = new ObjectMapper();

	private String getRootUrl() {
		return "http://localhost:" + port;
	}

	public AppTests() {
		
	}

	/**
	 * Confirm that all REST API endpoints are locked using Apache HttpClient
	 * 
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	@Test
	public void testAllApisLockedApache() throws ClientProtocolException, IOException {

		HttpUriRequest request = new HttpGet(getRootUrl() + API_TEST_ADMIN);
		CloseableHttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		assertThat(httpResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.UNAUTHORIZED.value()));

		request = new HttpGet(getRootUrl() + API_TEST_USER);
		httpResponse = HttpClientBuilder.create().build().execute(request);
		assertThat(httpResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.UNAUTHORIZED.value()));

		request = new HttpGet(getRootUrl() + API_TEST_CUSTOMER);
		httpResponse = HttpClientBuilder.create().build().execute(request);
		assertThat(httpResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.UNAUTHORIZED.value()));
	}

	/**
	 * Confirm that all REST API endpoints are locked using Spring
	 */
	@Test
	public void testAllApisLockedSpring() {

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		ResponseEntity<String> response = restTemplate.exchange(getRootUrl() + API_TEST_ADMIN, HttpMethod.GET, entity,
				String.class);
		assertThat(response.getStatusCode().value(), equalTo(HttpStatus.UNAUTHORIZED.value()));

		response = restTemplate.exchange(getRootUrl() + API_TEST_USER, HttpMethod.GET, entity, String.class);
		assertThat(response.getStatusCode().value(), equalTo(HttpStatus.UNAUTHORIZED.value()));

		response = restTemplate.exchange(getRootUrl() + API_TEST_CUSTOMER, HttpMethod.GET, entity, String.class);
		assertThat(response.getStatusCode().value(), equalTo(HttpStatus.UNAUTHORIZED.value()));
	}

	/**
	 * Confirm that all of the default users setup in 'import.sql' return 200
	 */
	@Test
	public void testAuthenticateAll() {

		ResponseEntity<AccessToken> response;
		AccessToken accessToken;

		AuthForm authForm = new AuthForm();
		authForm.setPassword("123456");

		authForm.setUsername("owner");
		response = restTemplate.postForEntity(getRootUrl() + API_AUTH_AUTHENTICATE, authForm, AccessToken.class);
		assertThat(response.getStatusCode().value(), equalTo(HttpStatus.OK.value()));
		accessToken = response.getBody();
		assertThat(accessToken.getAccessToken(),not(nullValue()));

		authForm.setUsername("admin");
		response = restTemplate.postForEntity(getRootUrl() + API_AUTH_AUTHENTICATE, authForm, AccessToken.class);
		assertThat(response.getStatusCode().value(), equalTo(HttpStatus.OK.value()));
		accessToken = response.getBody();
		assertThat(accessToken.getAccessToken(),not(nullValue()));

		authForm.setUsername("user");
		response = restTemplate.postForEntity(getRootUrl() + API_AUTH_AUTHENTICATE, authForm, AccessToken.class);
		assertThat(response.getStatusCode().value(), equalTo(HttpStatus.OK.value()));
		accessToken = response.getBody();
		assertThat(accessToken.getAccessToken(),not(nullValue()));

		authForm.setUsername("customer");
		response = restTemplate.postForEntity(getRootUrl() + API_AUTH_AUTHENTICATE, authForm, AccessToken.class);
		assertThat(response.getStatusCode().value(), equalTo(HttpStatus.OK.value()));
		accessToken = response.getBody();
		assertThat(accessToken.getAccessToken(),not(nullValue()));
	}

	/**
	 * Confirm that customer can access only /api/test/customer
	 */
	@Test
	public void testCustomerAccess() {

		ResponseEntity<AccessToken> accessToken;

		AuthForm authForm = new AuthForm();
		authForm.setPassword("123456");
		authForm.setUsername("customer");
		accessToken = restTemplate.postForEntity(getRootUrl() + API_AUTH_AUTHENTICATE, authForm, AccessToken.class);
		assertThat(accessToken.getStatusCode().value(), equalTo(HttpStatus.OK.value()));
		assertThat(accessToken.getBody().getAccessToken(),not(nullValue()));
		// customer authenticated OK
		// create entity of ROLE_CUSTOMER
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer "+accessToken.getBody().getAccessToken());
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		// call /api/test/user , expect 403 Forbidden
		ResponseEntity<String> response = restTemplate.exchange(getRootUrl() + API_TEST_USER, HttpMethod.GET, entity, String.class);
		assertThat(response.getStatusCode().value(), equalTo(HttpStatus.FORBIDDEN.value()));
		assertThat(response.getBody(),containsString("403"));
		// call /api/test/admin , expect 403 Forbidden
		response = restTemplate.exchange(getRootUrl() + API_TEST_ADMIN, HttpMethod.GET, entity, String.class);
		assertThat(response.getStatusCode().value(), equalTo(HttpStatus.FORBIDDEN.value()));
		assertThat(response.getBody(),containsString("403"));
		// call /api/test/customer , expect doest NOT contain 403
		response = restTemplate.exchange(getRootUrl() + API_TEST_CUSTOMER, HttpMethod.GET, entity, String.class);
		assertThat(response.getStatusCode().value(), equalTo(HttpStatus.OK.value()));
		assertThat(response.getBody(),not(containsString("403")));
	}

	/**
	 * Confirm that user can access only /api/test/user
	 */
	@Test
	public void testUserAccess() {

		ResponseEntity<AccessToken> accessToken;

		AuthForm authForm = new AuthForm();
		authForm.setPassword("123456");
		authForm.setUsername("user");
		accessToken = restTemplate.postForEntity(getRootUrl() + API_AUTH_AUTHENTICATE, authForm, AccessToken.class);
		assertThat(accessToken.getStatusCode().value(), equalTo(HttpStatus.OK.value()));
		assertThat(accessToken.getBody().getAccessToken(),not(nullValue()));
		// USER authenticated OK
		// create entity of ROLE_USER
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer "+accessToken.getBody().getAccessToken());
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		// call /api/test/customer , expect 403 Forbidden
		ResponseEntity<String> response = restTemplate.exchange(getRootUrl() + API_TEST_CUSTOMER, HttpMethod.GET, entity, String.class);
		assertThat(response.getStatusCode().value(), equalTo(HttpStatus.FORBIDDEN.value()));
		assertThat(response.getBody(),containsString("403"));
		// call /api/test/admin , expect 403 Forbidden
		response = restTemplate.exchange(getRootUrl() + API_TEST_ADMIN, HttpMethod.GET, entity, String.class);
		assertThat(response.getStatusCode().value(), equalTo(HttpStatus.FORBIDDEN.value()));
		assertThat(response.getBody(),containsString("403"));
		// call /api/test/user , expect doest NOT contain 403
		response = restTemplate.exchange(getRootUrl() + API_TEST_USER, HttpMethod.GET, entity, String.class);
		assertThat(response.getStatusCode().value(), equalTo(HttpStatus.OK.value()));
		assertThat(response.getBody(),not(containsString("403")));
	}

	/**
	 * Confirm that admin can access all APIs: 
	 * /api/test/user, /api/test/customer, /api/test/admin
	 */
	@Test
	public void testAdminAccess() {

		ResponseEntity<AccessToken> accessToken;

		AuthForm authForm = new AuthForm();
		authForm.setPassword("123456");
		authForm.setUsername("admin");
		accessToken = restTemplate.postForEntity(getRootUrl() + API_AUTH_AUTHENTICATE, authForm, AccessToken.class);
		assertThat(accessToken.getStatusCode().value(), equalTo(HttpStatus.OK.value()));
		assertThat(accessToken.getBody().getAccessToken(),not(nullValue()));
		// ADMIN authenticated OK
		// create entity of ROLE_ADMIN
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer "+accessToken.getBody().getAccessToken());
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		// call /api/test/customer , expect doest NOT contain 403
		ResponseEntity<String> response = restTemplate.exchange(getRootUrl() + API_TEST_CUSTOMER, HttpMethod.GET, entity, String.class);
		assertThat(response.getStatusCode().value(), equalTo(HttpStatus.OK.value()));
		assertThat(response.getBody(),not(containsString("403")));
		// call /api/test/admin , expect doest NOT contain 403
		response = restTemplate.exchange(getRootUrl() + API_TEST_ADMIN, HttpMethod.GET, entity, String.class);
		assertThat(response.getStatusCode().value(), equalTo(HttpStatus.OK.value()));
		assertThat(response.getBody(),not(containsString("403")));
		// call /api/test/user , expect doest NOT contain 403
		response = restTemplate.exchange(getRootUrl() + API_TEST_USER, HttpMethod.GET, entity, String.class);
		assertThat(response.getStatusCode().value(), equalTo(HttpStatus.OK.value()));
		assertThat(response.getBody(),not(containsString("403")));
	}

	/**
	 * Confirm that owner can access all APIs: 
	 * /api/test/user, /api/test/customer, /api/test/admin
	 */
	@Test
	public void testOwnerAccess() {

		ResponseEntity<AccessToken> accessToken;

		AuthForm authForm = new AuthForm();
		authForm.setPassword("123456");
		authForm.setUsername("admin");
		accessToken = restTemplate.postForEntity(getRootUrl() + API_AUTH_AUTHENTICATE, authForm, AccessToken.class);
		assertThat(accessToken.getStatusCode().value(), equalTo(HttpStatus.OK.value()));
		assertThat(accessToken.getBody().getAccessToken(),not(nullValue()));
		// OWNER authenticated OK
		// create entity of ROLE_OWNER
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer "+accessToken.getBody().getAccessToken());
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		// call /api/test/customer , expect doest NOT contain 403
		ResponseEntity<String> response = restTemplate.exchange(getRootUrl() + API_TEST_CUSTOMER, HttpMethod.GET, entity, String.class);
		assertThat(response.getStatusCode().value(), equalTo(HttpStatus.OK.value()));
		assertThat(response.getBody(),not(containsString("403")));
		// call /api/test/admin , expect doest NOT contain 403
		response = restTemplate.exchange(getRootUrl() + API_TEST_ADMIN, HttpMethod.GET, entity, String.class);
		assertThat(response.getStatusCode().value(), equalTo(HttpStatus.OK.value()));
		assertThat(response.getBody(),not(containsString("403")));
		// call /api/test/user , expect doest NOT contain 403
		response = restTemplate.exchange(getRootUrl() + API_TEST_USER, HttpMethod.GET, entity, String.class);
		assertThat(response.getStatusCode().value(), equalTo(HttpStatus.OK.value()));
		assertThat(response.getBody(),not(containsString("403")));
	}

}
