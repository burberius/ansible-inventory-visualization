package net.troja.ansinvisual;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class HostsFileReaderTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	private HostsFileReader classToTest = new HostsFileReader();
	
	@Test
	public void getContent() throws IOException {
		Map<String, List<String>> result = classToTest.getContent("src/test/resources/testhosts.txt");
		
		assertThat(result.size(), equalTo(3));
		
		List<String> list = result.get("test");
		assertThat(list.size(), equalTo(2));
		
		list = result.get("demo");
		assertThat(list.size(), equalTo(10));
		assertThat(list.get(0), equalTo("demo0.troja.net"));

		list = result.get("alpha");
		assertThat(list.size(), equalTo(26));
		assertThat(list.get(0), equalTo("alphaa.troja.net"));
	}
	
	@Test
	public void getExpandedNumeric() {
		List<String> list = classToTest.getExpanded("test[3:7]");
		
		assertThat(list.size(), equalTo(5));
	}
	
	@Test
	public void getExpandedNumericPre0() {
		List<String> list = classToTest.getExpanded("test[006:010].no");
		
		assertThat(list.size(), equalTo(5));
		assertThat(list.get(0), equalTo("test006.no"));
	}
	
	@Test
	public void getExpandedAlphabetic() {
		List<String> list = classToTest.getExpanded("test[g:z]");
		
		assertThat(list.size(), equalTo(20));
		assertThat(list.get(0), equalTo("testg"));
	}
	
	@Test
	public void getExpandedException() {
		String input = "test[%:z]";
		
		thrown.expect(IllegalArgumentException.class);
	    thrown.expectMessage("Could not understand "+ input);
				
		classToTest.getExpanded(input);
	}
}
