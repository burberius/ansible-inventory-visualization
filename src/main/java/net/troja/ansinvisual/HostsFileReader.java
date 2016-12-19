package net.troja.ansinvisual;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class HostsFileReader {
	private Map<String, List<String>> result = new HashMap<>();
	
	public Map<String, List<String>> getContent(String fileName) throws IOException {
		Path path = Paths.get(fileName);
		Pattern groupPattern = Pattern.compile("\\[(.*)\\]");
		Pattern hostPattern = Pattern.compile("^(\\S*)");
		
		String currentGroup = null;
		
		List<String> lines = Files.readAllLines(path);
		for( String line : lines) {
			if (line.startsWith("#") || StringUtils.isBlank(line)) {
				continue;
			}
			Matcher matcher = groupPattern.matcher(line);
			if(matcher.matches()) {
				currentGroup = matcher.group(1);
			} else {
				Matcher hostMatcher = hostPattern.matcher(line);
				if(hostMatcher.matches()) {
					addHost(currentGroup, hostMatcher.group(1));
				}
			}
		}
		return result;
	}

	private void addHost(String group, String host) {
		if(group == null) {
			throw new RuntimeException("Could not identify group for host " + host);
		}
		List<String> hosts = result.get(group);
		if(hosts == null) {
			hosts = new ArrayList<>();
			result.put(group, hosts);
		}
		hosts.addAll(getExpanded(host));
	}
	
	public List<String> getExpanded(String input) {
		List<String> result = new ArrayList<>();
		Matcher matcher = Pattern.compile("(.*)\\[(.*):(.*)\\](.*)").matcher(input);
		if(matcher.matches()) {
			String start = matcher.group(1);
			String from = matcher.group(2);
			String to = matcher.group(3);
			String end = matcher.group(4);
			if(from.matches("^\\d*$") && to.matches("^\\d*$")) {
				int length = 0;
				if(from.startsWith("0")) {
					length = from.length();
				}
				int fromNum = Integer.parseInt(from);
				int toNum = Integer.parseInt(to);
				for(int pos = fromNum; pos <= toNum; pos++) {
					if(length > 0) {
						result.add(start + String.format("%0" + length + "d", pos) + end);
					} else {
						result.add(start + pos + end);
					}
				}
			} else if(from.matches("^[a-z]$") && to.matches("^[a-z]$")) {
				int fromNum = from.charAt(0);
				int toNum = to.charAt(0);
				for(int pos = fromNum; pos <= toNum; pos++) {
					result.add(start + (char)pos + end);
				}
			} else {
				throw new IllegalArgumentException("Could not understand " + input);
			}
		} else {
			result.add(input);
		}
		return result;
	}
}
