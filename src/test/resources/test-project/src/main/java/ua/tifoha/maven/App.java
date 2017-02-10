package ua.tifoha.maven;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) throws NotFoundException, CannotCompileException, IOException {
		ClassPool pool = ClassPool.getDefault();
		// 解压jar包后的路径
		pool.insertClassPath("D:\\opt\\pool\\jprofiler");

		CtClass cc = pool.get("com.ejt.framework.e.c");

		CtClass[] param = new CtClass[3];
		param[0] = pool.get("java.lang.String");
		param[1] = pool.get("java.lang.String");
		param[2] = pool.get("java.lang.String");

		assert cc != null;
		CtMethod method = cc.getDeclaredMethod("a", param);

		assert method != null;
		method.setBody("{return 1;}");

		cc.writeFile("D:\\opt\\pool\\modified");
	}
//    public static void main(String[] args) {
//            EventType.SIGN_UP.getId();
//            System.out.println(Stream.of(EventType.values()).max(Comparator.comparingInt(EventType::getId)).get().getId());
//        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(new CustomBeanFactory());
//        ctx.register(AppContext.class);
//        ctx.refresh();
//        SimpleDependentBean dependentBean = ctx.getBean(SimpleDependentBean.class);
//        System.out.println(dependentBean);
//    }
//
//    @Configuration
//    public static class AppContext {
//        @Bean
//        public SimpleBean simpleBean() {
//            return new SimpleBean();
//        }
//
//        @Bean
////        @Scope("request")
//        public SimpleDependentBean simpleDependentBean() {
//            return new SimpleDependentBean();
//        }
//
//    }
//
//    public static class SimpleBean {
//        public SimpleBean() {
//        }
//
//        private String name;
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//    }
//
//    public static class SimpleDependentBean {
//        public SimpleDependentBean() {
//        }
//
//        @Autowired
//        private SimpleBean simpleBean;
//
//        public SimpleBean getSimpleBean() {
//            return simpleBean;
//        }
//
//        public void setSimpleBean(SimpleBean simpleBean) {
//            this.simpleBean = simpleBean;
//        }
//    }
//
//}
//
//class CustomBeanFactory extends DefaultListableBeanFactory {
//    Map<String, Object> proxies = new LinkedHashMap<>();
//    Map<String, Object> mocks = new LinkedHashMap<>();
//    @Override
//    protected Object createBean(String beanName, RootBeanDefinition mbd, Object[] args) throws BeanCreationException {
//        Object bean = super.createBean(beanName, mbd, args);
//        Object proxy = Enhancer.create(mbd.getBeanClass(), new MyInvocationHandler(bean));
//        proxies.put(beanName, proxy);
//        return proxy;
//    }
//
//    public Object mock(String beanName) {
//        Object bean = proxies.get(beanName);
//        Object mock = Mockito.mock(bean.getClass().get);
//    }
//}
//
//class MyInvocationHandler implements MethodInterceptor {
//    public MyInvocationHandler(Object target) {
//        this.target = target;
//    }
//
//    private Object target;
//    @Override
//    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
//        return method.invoke(target, objects);
//    }
//
//    public Object getTarget() {
//        return target;
//    }
//
//    public void setTarget(Object target) {
//        this.target = target;
//    }
}
