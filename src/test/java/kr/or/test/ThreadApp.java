package kr.or.test;

public class ThreadApp {

	public static void main(String[] args) {
		//메인스레드 시작 진입접 main()
		//(new ThreadApp()).start();//아래 2줄과 이 1줄은 같음.
		MyThread myThread1 = new MyThread("myThread1");
		MyThread myThread2 = new MyThread("myThread2");
		MyThread myThread3 = new MyThread("myThread3");
		myThread1.start();
		myThread2.start();
		myThread3.start();
		System.out.println("언제 출력될까요?");
	}
}

class MyThread extends Thread {
	public MyThread(String szName) {
		super();
	}
	public void run() {//myThread스레드를 start()호출해야지만, 자동실행되는 매서드
		for(int cnt=0;cnt<100;cnt++) {
			System.out.print(this.getName() + " ");
			
		}
	}
} 