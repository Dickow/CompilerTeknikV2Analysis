class testmain {
	
	public static void main (String[] args) {
		
		A a;
		B b;
		
		System.out.print("Hello ");
		System.out.println("World");
		
		a = new A();
		a.initialize();
		a.test("Hello", 42);
		
		a.test(b.spas(true), b.spas());
		
		return;
	}
}

class A {
	
	String x;
	
	public void initialize() {
		x="";
		return;
		
		
	}
	
	public void test(String argument, int number) {
		int counter;
		
		int a;
		x = argument;
		
		counter = 0;
		
		if (!number){
			a = 2;
		}
		
		while ( counter < number ) {
			if (counter < number - counter) {
				System.out.println(".");
			}
			
			if (!(counter < number - counter)) {
				System.out.println(counter);
			} else {
				System.out.println("-");
			}
			
			counter = counter+1;
		}
		
		counter = -number;
		
		if (!(counter<0)) {
			System.out.println(x);
		}
		
		return;
	}
	
}

class B {
	
	int y;
	
	public void test2(int a, int b){
		return;
	}
	
	public void test2(String a, String b){
		return;
	}
	
	public int spas(){
		return (12);
	}
	
	public String spas(boolean b){
		String answer;
		
		if (b){
			answer = "yes";
		} else {
			answer = "no";
		}
		
		return(answer);
	}
	
}