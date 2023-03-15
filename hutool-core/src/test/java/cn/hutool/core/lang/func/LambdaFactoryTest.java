package cn.hutool.core.lang.func;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.reflect.MethodHandleUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.invoke.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author nasodaengineer
 */
public class LambdaFactoryTest {

	@Test
	public void testMethodNotMatch() {
		try {
			LambdaFactory.build(Function.class, Something.class, "setId", Long.class);
		} catch (final Exception e) {
			Assert.assertTrue(e.getCause() instanceof LambdaConversionException);
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void buildLambdaTest() {
		final Something something = new Something();
		something.setId(1L);
		something.setName("name");

		final Function<Something, Long> get11 = LambdaFactory.build(Function.class, Something.class, "getId");
		final Function<Something, Long> get12 = LambdaFactory.build(Function.class, Something.class, "getId");

		Assert.assertEquals(get11, get12);
		// 通过LambdaFactory模拟创建一个getId方法的Lambda句柄函数，通过调用这个函数，实现方法调用。
		Assert.assertEquals(something.getId(), get11.apply(something));

		final String name = "sname";
		final BiConsumer<Something, String> set = LambdaFactory.build(BiConsumer.class, Something.class, "setName", String.class);
		set.accept(something, name);

		Assert.assertEquals(something.getName(), name);
	}

	@Data
	private static class Something {
		private Long id;
		private String name;
	}

	/**
	 * 简单的性能测试，大多数情况下直接调用 快于 lambda 快于 反射 快于 代理
	 *
	 * @author nasodaengineer
	 */
	@RunWith(Parameterized.class)
	@Ignore
	public static class PerformanceTest {

		@Parameterized.Parameter
		public int count;

		@Parameterized.Parameters
		public static Collection<Integer> parameters() {
			return ListUtil.of(1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000);
		}

		/**
		 * <p>lambda     运行1次耗时 7000 NANOSECONDS
		 * <p>reflect    运行1次耗时 11300 NANOSECONDS
		 * <p>hardCode   运行1次耗时 12800 NANOSECONDS
		 * <p>proxy      运行1次耗时 160200 NANOSECONDS
		 * <p>mh         运行1次耗时 197900 NANOSECONDS
		 * <p>--------------------------------------------
		 * <p>hardCode   运行10次耗时 1500 NANOSECONDS
		 * <p>lambda     运行10次耗时 2200 NANOSECONDS
		 * <p>mh         运行10次耗时 11700 NANOSECONDS
		 * <p>proxy      运行10次耗时 14400 NANOSECONDS
		 * <p>reflect    运行10次耗时 28600 NANOSECONDS
		 * <p>--------------------------------------------
		 * <p>lambda     运行100次耗时 9300 NANOSECONDS
		 * <p>hardCode   运行100次耗时 14400 NANOSECONDS
		 * <p>mh         运行100次耗时 42900 NANOSECONDS
		 * <p>proxy      运行100次耗时 107900 NANOSECONDS
		 * <p>reflect    运行100次耗时 430800 NANOSECONDS
		 * <p>--------------------------------------------
		 * <p>hardCode   运行1000次耗时 86300 NANOSECONDS
		 * <p>lambda     运行1000次耗时 101700 NANOSECONDS
		 * <p>reflect    运行1000次耗时 754700 NANOSECONDS
		 * <p>mh         运行1000次耗时 962200 NANOSECONDS
		 * <p>proxy      运行1000次耗时 1200500 NANOSECONDS
		 * <p>--------------------------------------------
		 * <p>hardCode   运行10000次耗时 333000 NANOSECONDS
		 * <p>lambda     运行10000次耗时 367800 NANOSECONDS
		 * <p>mh         运行10000次耗时 999100 NANOSECONDS
		 * <p>proxy      运行10000次耗时 2766100 NANOSECONDS
		 * <p>reflect    运行10000次耗时 3157200 NANOSECONDS
		 * <p>--------------------------------------------
		 * <p>lambda     运行100000次耗时 571600 NANOSECONDS
		 * <p>hardCode   运行100000次耗时 1061700 NANOSECONDS
		 * <p>reflect    运行100000次耗时 1326800 NANOSECONDS
		 * <p>proxy      运行100000次耗时 3160900 NANOSECONDS
		 * <p>mh         运行100000次耗时 4137500 NANOSECONDS
		 * <p>--------------------------------------------
		 * <p>hardCode   运行1000000次耗时 5066200 NANOSECONDS
		 * <p>lambda     运行1000000次耗时 5868700 NANOSECONDS
		 * <p>mh         运行1000000次耗时 8342700 NANOSECONDS
		 * <p>reflect    运行1000000次耗时 13009400 NANOSECONDS
		 * <p>proxy      运行1000000次耗时 21787800 NANOSECONDS
		 * <p>--------------------------------------------
		 * <p>hardCode   运行10000000次耗时 51102700 NANOSECONDS
		 * <p>lambda     运行10000000次耗时 55007900 NANOSECONDS
		 * <p>mh         运行10000000次耗时 72751700 NANOSECONDS
		 * <p>reflect    运行10000000次耗时 92348800 NANOSECONDS
		 * <p>proxy      运行10000000次耗时 199705500 NANOSECONDS
		 * <p>--------------------------------------------
		 * <p>hardCode   运行100000000次耗时 456094400 NANOSECONDS
		 * <p>lambda     运行100000000次耗时 562348600 NANOSECONDS
		 * <p>reflect    运行100000000次耗时 630433200 NANOSECONDS
		 * <p>mh         运行100000000次耗时 671914300 NANOSECONDS
		 * <p>proxy      运行100000000次耗时 1117192600 NANOSECONDS
		 * <p>--------------------------------------------
		 */
		@SuppressWarnings({"rawtypes", "unchecked", "Convert2MethodRef"})
		@Test
		@SneakyThrows
		@Ignore
		public void lambdaGetPerformanceTest() {
			final Something something = new Something();
			something.setId(1L);
			something.setName("name");
			final Method getByReflect = Something.class.getMethod("getId");
			final MethodHandle getByMh = MethodHandleUtil.findMethod(Something.class, "getId", MethodType.methodType(Long.class));
			final Function getByProxy = MethodHandleProxies.asInterfaceInstance(Function.class, MethodHandles.lookup().unreflect(getByReflect));
			final Function getByLambda = LambdaFactory.build(Function.class, getByReflect);
			final Task lambdaTask = new Task("lambda", () -> getByLambda.apply(something));
			final Task mhTask = new Task("mh", () -> getByMh.invoke(something));
			final Task proxyTask = new Task("proxy", () -> getByProxy.apply(something));
			final Task reflectTask = new Task("reflect", () -> getByReflect.invoke(something));
			final Task hardCodeTask = new Task("hardCode", () -> something.getId());
			final Task[] tasks = {hardCodeTask, lambdaTask, mhTask, proxyTask, reflectTask};
			loop(count, tasks);
		}

		@Test
		public void testConstructor() {
			Constructor<Something> constructor = ((SerSupplier<Constructor<Something>>) Something.class::getConstructor).get();
			Supplier<Something> constructorLambda = LambdaFactory.build(Supplier.class, constructor);
			// constructorLambda can be cache or transfer
			Something something = constructorLambda.get();
			Assert.assertEquals(Something.class, something.getClass());
		}

		/**
		 * <p>hardCode   运行1次耗时 7600 NANOSECONDS
		 * <p>lambda     运行1次耗时 12400 NANOSECONDS
		 * <p>reflect    运行1次耗时 19900 NANOSECONDS
		 * <p>mh         运行1次耗时 139900 NANOSECONDS
		 * <p>proxy      运行1次耗时 261300 NANOSECONDS
		 * <p>--------------------------------------------
		 * <p>hardCode   运行10次耗时 1700 NANOSECONDS
		 * <p>lambda     运行10次耗时 2600 NANOSECONDS
		 * <p>mh         运行10次耗时 3900 NANOSECONDS
		 * <p>proxy      运行10次耗时 20400 NANOSECONDS
		 * <p>reflect    运行10次耗时 26500 NANOSECONDS
		 * <p>--------------------------------------------
		 * <p>hardCode   运行100次耗时 9000 NANOSECONDS
		 * <p>lambda     运行100次耗时 16900 NANOSECONDS
		 * <p>mh         运行100次耗时 32200 NANOSECONDS
		 * <p>proxy      运行100次耗时 315700 NANOSECONDS
		 * <p>reflect    运行100次耗时 604300 NANOSECONDS
		 * <p>--------------------------------------------
		 * <p>hardCode   运行1000次耗时 123500 NANOSECONDS
		 * <p>lambda     运行1000次耗时 253100 NANOSECONDS
		 * <p>mh         运行1000次耗时 644600 NANOSECONDS
		 * <p>reflect    运行1000次耗时 793100 NANOSECONDS
		 * <p>proxy      运行1000次耗时 1111100 NANOSECONDS
		 * <p>--------------------------------------------
		 * <p>hardCode   运行10000次耗时 346800 NANOSECONDS
		 * <p>lambda     运行10000次耗时 524900 NANOSECONDS
		 * <p>mh         运行10000次耗时 931000 NANOSECONDS
		 * <p>reflect    运行10000次耗时 2046500 NANOSECONDS
		 * <p>proxy      运行10000次耗时 3108400 NANOSECONDS
		 * <p>--------------------------------------------
		 * <p>lambda     运行100000次耗时 608300 NANOSECONDS
		 * <p>hardCode   运行100000次耗时 1095600 NANOSECONDS
		 * <p>mh         运行100000次耗时 1430100 NANOSECONDS
		 * <p>reflect    运行100000次耗时 1558400 NANOSECONDS
		 * <p>proxy      运行100000次耗时 5566000 NANOSECONDS
		 * <p>--------------------------------------------
		 * <p>lambda     运行1000000次耗时 6261000 NANOSECONDS
		 * <p>hardCode   运行1000000次耗时 6570200 NANOSECONDS
		 * <p>mh         运行1000000次耗时 8703300 NANOSECONDS
		 * <p>reflect    运行1000000次耗时 16437800 NANOSECONDS
		 * <p>proxy      运行1000000次耗时 22161100 NANOSECONDS
		 * <p>--------------------------------------------
		 * <p>lambda     运行10000000次耗时 60895800 NANOSECONDS
		 * <p>hardCode   运行10000000次耗时 61055300 NANOSECONDS
		 * <p>mh         运行10000000次耗时 69782400 NANOSECONDS
		 * <p>reflect    运行10000000次耗时 78078800 NANOSECONDS
		 * <p>proxy      运行10000000次耗时 193799800 NANOSECONDS
		 * <p>--------------------------------------------
		 * <p>hardCode   运行100000000次耗时 499826200 NANOSECONDS
		 * <p>lambda     运行100000000次耗时 537454100 NANOSECONDS
		 * <p>reflect    运行100000000次耗时 673561400 NANOSECONDS
		 * <p>mh         运行100000000次耗时 700774100 NANOSECONDS
		 * <p>proxy      运行100000000次耗时 1169452400 NANOSECONDS
		 * <p>--------------------------------------------
		 */
		@SuppressWarnings({"rawtypes", "unchecked"})
		@Test
		@SneakyThrows
		@Ignore
		public void lambdaSetPerformanceTest() {
			final Something something = new Something();
			something.setId(1L);
			something.setName("name");
			final Method setByReflect = Something.class.getMethod("setName", String.class);
			final MethodHandle setByMh = MethodHandleUtil.findMethod(Something.class, "setName", MethodType.methodType(Void.TYPE, String.class));
			final BiConsumer setByProxy = MethodHandleProxies.asInterfaceInstance(BiConsumer.class, setByMh);
			final BiConsumer setByLambda = LambdaFactory.build(BiConsumer.class, setByReflect);
			final String name = "name1";
			final Task lambdaTask = new Task("lambda", () -> {
				setByLambda.accept(something, name);
				return null;
			});
			final Task proxyTask = new Task("proxy", () -> {
				setByProxy.accept(something, name);
				return null;
			});
			final Task mhTask = new Task("mh", () -> {
				setByMh.invoke(something, name);
				return null;
			});
			final Task reflectTask = new Task("reflect", () -> {
				setByReflect.invoke(something, name);
				return null;
			});
			final Task hardCodeTask = new Task("hardCode", () -> {
				something.setName(name);
				return null;
			});
			final Task[] tasks = {hardCodeTask, lambdaTask, proxyTask, mhTask, reflectTask};
			loop(count, tasks);
		}

		@SuppressWarnings("rawtypes")
		@SneakyThrows
		private void loop(final int count, final Task... tasks) {
			Arrays.stream(tasks)
					.peek(task -> {
						final LambdaFactoryTest.SupplierThrowable runnable = task.getRunnable();
						long cost = System.nanoTime();
						for (int i = 0; i < count; i++) {
							runnable.get();
						}
						cost = System.nanoTime() - cost;
						task.setCost(cost);
						task.setCount(count);
					})
					.sorted(Comparator.comparing(Task::getCost))
					.map(Task::format)
					.forEach(System.out::println);
			System.out.println("--------------------------------------------");
		}

		@SuppressWarnings({"InnerClassMayBeStatic", "FieldMayBeFinal"})
		@Getter
		private class Task {
			private String name;
			private LambdaFactoryTest.SupplierThrowable<?> runnable;
			@Setter
			private long cost;
			@Setter
			private Integer count;

			public Task(final String name, final LambdaFactoryTest.SupplierThrowable<?> runnable) {
				this.name = name;
				this.runnable = runnable;
			}

			public String format() {
				final TimeUnit timeUnit = TimeUnit.NANOSECONDS;
				return String.format("%-10s 运行%d次耗时 %d %s", name, count, timeUnit.convert(cost, TimeUnit.NANOSECONDS), timeUnit.name());
			}
		}

	}

	@FunctionalInterface
	interface SupplierThrowable<T> {
		T get0() throws Throwable;

		default T get() {
			try {
				return get0();
			} catch (final Throwable e) {
				throw new RuntimeException(e);
			}
		}
	}
}
