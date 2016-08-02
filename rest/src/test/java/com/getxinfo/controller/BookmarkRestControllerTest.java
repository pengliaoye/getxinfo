package com.getxinfo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.getxinfo.Account;
import com.getxinfo.AccountRepository;
import com.getxinfo.Bookmark;
import com.getxinfo.BookmarkRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BookmarkRestControllerTest {

	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	private MockMvc mockMvc;

	private String userName = "bdussault";

	private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;

	private Account account;

	private List<Bookmark> bookmarkList = new ArrayList<>();

	@Autowired
	private BookmarkRepository bookmarkRepository;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	void setConverters(HttpMessageConverter<?>[] converters) {

		this.mappingJackson2HttpMessageConverter = (MappingJackson2HttpMessageConverter) Arrays.asList(converters).stream()
				.filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().get();

		Assert.assertNotNull("the JSON message converter must not be null", this.mappingJackson2HttpMessageConverter);
	}

	@Before
	public void setup() throws Exception {
		this.mockMvc = webAppContextSetup(webApplicationContext).build();

		this.bookmarkRepository.deleteAllInBatch();
		this.accountRepository.deleteAllInBatch();

		this.account = accountRepository.save(new Account(userName, "password"));
		this.bookmarkList.add(
				bookmarkRepository.save(new Bookmark(account, "http://bookmark.com/1/" + userName, "A description")));
		this.bookmarkList.add(
				bookmarkRepository.save(new Bookmark(account, "http://bookmark.com/2/" + userName, "A description")));
	}

	@Test
	public void userNotFound() throws Exception {
		mockMvc.perform(post("/george/bookmarks/").content(this.json(new Bookmark())).contentType(contentType))
				.andExpect(status().isNotFound());
	}

	protected String json(Object o) throws IOException {
		MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
		this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
		return mockHttpOutputMessage.getBodyAsString();
	}
}
