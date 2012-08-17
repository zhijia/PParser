
class Token {
	String type;
	String value;
	Token(String type, String value)
	{
		this.type = type;
		this.value = value;
	}
	public String toString()
	{
		return type+":"+value;
	}
}
