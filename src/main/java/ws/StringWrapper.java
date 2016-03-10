package ws;

import javax.xml.bind.annotation.XmlRootElement;

//for Angular: You cannot result with List<String> or List<Integer> and so on...
//this is because non of the wrapper classes got the @XMLRootElement annotation in it.
//that's why we provide our own wrapper:


@XmlRootElement
public class StringWrapper {

	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
}
