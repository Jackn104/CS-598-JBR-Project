package testers;

public class ExampleCode
{
	public static void main(String[] args) {

		new Print().bar();
		int x = 20;
		int y = 10;
		math(x,y);

	}

	public static void math(int x, int y) {
		int sum = x+y;
		int mul = x*y;
		int sub = x-y;
		new Print().foo();
	}

}

class Print
{
	public void foo() {
		bar();
	}

	public void bar() {
	}

}