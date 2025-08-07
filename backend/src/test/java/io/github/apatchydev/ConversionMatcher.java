package io.github.apatchydev;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;

public class ConversionMatcher<T, R> {
    // For lambda without try catch
    @FunctionalInterface
    public interface ThrowingFunction<T, R, E extends Exception> {
        R apply(T t) throws E;
    }

    // Field & Constructor

    private final ThrowingFunction<T, R,? extends Exception> converter;

    private ConversionMatcher(ThrowingFunction<T, R, ? extends Exception> converter) {
        this.converter = converter;
    }

    // Factory methods

    public static <T, R> ConversionMatcher<T, R> convertedBy(ThrowingFunction<T, R, ? extends Exception> converter) {
        return new ConversionMatcher<>(converter);
    }

    public static <T, R> Matcher<T> convertibleBy(ThrowingFunction<T, R, ? extends Exception> converter, Class<? super R> type) {
        return convertedBy(converter).item(Matchers.instanceOf(type));
    }

    // Methods

    public Matcher<T> item(Matcher<? super R> matcher) {
        return new TypeSafeMatcher<T>() {
            @Override
            protected boolean matchesSafely(T item) {
                try {
                    return matcher.matches(converter.apply(item));
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a string which, when parsed, matches ").appendDescriptionOf(matcher);
            }

            @Override
            protected void describeMismatchSafely(T item, Description mismatchDescription) {
                try {
                    R converted = converter.apply(item);
                    // Match failure due to subsequent matcher
                    matcher.describeMismatch(converted, mismatchDescription);
                } catch (Exception e) {
                    // Match failure due to conversion error
                    mismatchDescription.appendText("could not be parsed: ").appendText(e.getMessage());
                }
            }
        };
    }
}
