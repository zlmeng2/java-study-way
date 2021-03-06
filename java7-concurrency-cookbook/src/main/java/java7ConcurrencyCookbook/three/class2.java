package java7ConcurrencyCookbook.three;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 课程：java并发编程7学习笔记(http://ifeve.com/java-7-concurrency-cookbook/)
 * 章节：2.7使用读/写锁同步数据访问
 * 替换：[0-9]{1,2}\n
 */
@Slf4j
@SuppressWarnings("Duplicates")
public class class2 {

    //1.创建一个会实现print queue的类名为 PrintQueue。
    class PrintQueue {
        //2.声明一个对象为Semaphore，称它为semaphore。
        private final Semaphore semaphore;

        //3.如我们之前提到的，你将实现semaphores来修改print queue例子。打开PrintQueue类并声明一个boolean array名为 freePrinters。这个array储存空闲的等待打印任务的和正在打印文档的printers。
        private boolean freePrinters[];

        //4.接着，声明一个名为lockPrinters的Lock对象。将要使用这个对象来保护freePrinters array的访问。
        private Lock lockPrinters;

        //5.修改类的构造函数并初始化新声明的对象们。freePrinters array 有3个元素，全部初始为真值。semaphore用3作为它的初始值。
        public PrintQueue() {
            semaphore = new Semaphore(3);
            freePrinters = new boolean[3];
            for (int i = 0; i < 3; i++) {
                freePrinters[i] = true;
            }
            lockPrinters = new ReentrantLock();
        }

        //6.修改printJob()方法。它接收一个称为document的对象最为唯一参数。
        public void printJob(Object document) {
            //7.首先，调用acquire()方法获得semaphore的访问。由于此方法会抛出 InterruptedException异常，所以必须加入处理它的代码。
            try {
                semaphore.acquire();
                //8.接着使用私有方法 getPrinter()来获得被安排打印任务的打印机的号码。
                int assignedPrinter = getPrinter();
                //9. 然后， 随机等待一段时间来实现模拟打印文档的行。
                long duration = (long) (Math.random() * 10);
                log.info("{}: PrintQueue: Printing a Job in Printer{} during {} seconds", Thread.currentThread().getName(), assignedPrinter, duration);
                TimeUnit.SECONDS.sleep(duration);
                //10.最后，调用release() 方法来解放semaphore并标记打印机为空闲，通过在对应的freePrinters array引索内分配真值。
                freePrinters[assignedPrinter] = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                semaphore.release();
            }
        }

        //11.实现 getPrinter() 方法。它是一个私有方法，返回一个int值，并不接收任何参数。
        private int getPrinter() {
            //12. 首先，声明一个int变量来保存printer的引索值。
            int ret = -1;
            //13. 然后， 获得lockPrinters对象 object的访问。
            try {
                lockPrinters.lock();
                //14.然后，在freePrinters array内找到第一个真值并在一个变量中保存这个引索值。修改值为false，因为等会这个打印机就会被使用。
                for (int i = 0; i < freePrinters.length; i++) {
                    if (freePrinters[i]) {
                        ret = i;
                        freePrinters[i] = false;
                        break;
                    }
                }
                //15.最后，解放lockPrinters对象并返回引索对象为真值。
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lockPrinters.unlock();
            }
            return ret;
        }
    }


    //16.创建一个名为Job的类并一定实现Runnable 接口。这个类实现把文档传送到打印机的任务。
    class Job implements Runnable {

        //17.声明一个对象为PrintQueue，名为printQueue。
        private PrintQueue printQueue;

        //18.实现类的构造函数，初始化这个类里的PrintQueue对象。
        public Job(PrintQueue printQueue) {
            this.printQueue = printQueue;
        }

        //19.实现方法run()。
        @Override
        public void run() {
            //20.首先， 此方法写信息到操控台表明任务已经开始执行了。
            log.info("{}: Going to print a job", Thread.currentThread().getName());
            //21.然后，调用PrintQueue 对象的printJob()方法。
            printQueue.printJob(new Object());
            //22.最后， 此方法写信息到操控台表明它已经结束运行了。
            log.info("{}: The document has been printed", Thread.currentThread().getName());
        }

    }

    //23.实现例子的main类，创建名为 Main的类并实现main()方法。
    @Test
    public void test () {
        //24.创建PrintQueue对象名为printQueue。
        PrintQueue printQueue = new PrintQueue();

        //25.创建10个threads。每个线程会执行一个发送文档到print queue的Job对象，并开始这10个线程们。
        Thread thread[] = new Thread[3];
        for (int i = 0; i < thread.length; i++) {
            thread[i] = new Thread(new Job(printQueue), "Thread" + i);
            thread[i].start();
        }
        try {
            //26.最后，等待线程完成
            for (int i = 0; i < thread.length; i++) {
                thread[i].join();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test1(){
        log.info("1111111");
        System.out.println("12412412");
    }

}
