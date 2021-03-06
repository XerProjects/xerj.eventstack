package io.github.xerprojects.xerj.eventstack.providers;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.StreamSupport;

import io.github.xerprojects.xerj.eventstack.EventHandler;
import io.github.xerprojects.xerj.eventstack.entities.TestEvent;
import io.github.xerprojects.xerj.eventstack.entities.TestEventHandler;
import io.github.xerprojects.xerj.eventstack.providers.CompositeEventHandlerProvider;
import io.github.xerprojects.xerj.eventstack.providers.RegistryEventHandlerProvider;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class CompositeEventHandlerProviderTests {
    @Nested
	public class Constructor {
		@Test
		public void shouldThrowWhenRegistryConfigurationArgumentIsNull() {
			assertThrows(IllegalArgumentException.class, () -> {
				new CompositeEventHandlerProvider(null);
			});
		}
	}
	
	@Nested
	public class GetEventHandlersForMethod {
		
		@Test
		public void shouldProvideRegisteredEventHandlers() {
			
			var testEventHandler1 = new TestEventHandler();
			var testEventHandler2 = new TestEventHandler();
			
			var provider1 = new RegistryEventHandlerProvider(registry ->
				registry.registerEventHandler(TestEvent.class, () -> testEventHandler1)
                    .registerEventHandler(TestEvent.class, () -> testEventHandler2));

            var compositeProvider = new CompositeEventHandlerProvider(List.of(provider1));
			
			Iterable<EventHandler<TestEvent>> resolvedHandlers = compositeProvider.getEventHandlersFor(TestEvent.class);			
			
			assertTrue(resolvedHandlers.iterator().hasNext());
            assertTrue(StreamSupport.stream(resolvedHandlers.spliterator(), false)
                .allMatch(h -> h == testEventHandler1 || h == testEventHandler2));
        }
        
        
		
		@Test
		public void shouldProvideRegisteredEventHandlersFromMultipleProviders() {
			
			var testEventHandler1 = new TestEventHandler();
			var testEventHandler2 = new TestEventHandler();
			var testEventHandler3 = new TestEventHandler();
			var testEventHandler4 = new TestEventHandler();
			
			var provider1 = new RegistryEventHandlerProvider(registry ->
				registry.registerEventHandler(TestEvent.class, () -> testEventHandler1)
                    .registerEventHandler(TestEvent.class, () -> testEventHandler2));
                    
			var provider2 = new RegistryEventHandlerProvider(registry ->
                registry.registerEventHandler(TestEvent.class, () -> testEventHandler3)
                    .registerEventHandler(TestEvent.class, () -> testEventHandler4));

            var compositeProvider = new CompositeEventHandlerProvider(List.of(provider1, provider2));
			
			Iterable<EventHandler<TestEvent>> resolvedHandlers = compositeProvider.getEventHandlersFor(TestEvent.class);			
			
			assertTrue(resolvedHandlers.iterator().hasNext());
            assertTrue(StreamSupport.stream(resolvedHandlers.spliterator(), false)
                .allMatch(h -> h == testEventHandler1 || h == testEventHandler2 || h == testEventHandler3 || h == testEventHandler4));
		}
	
	}
}