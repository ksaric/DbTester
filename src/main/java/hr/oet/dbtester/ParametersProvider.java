package hr.oet.dbtester;

import javax.inject.Provider;
import java.util.List;

/**
 * User: ksaric
 */
public interface ParametersProvider<T extends List<String>> extends Provider<T> {
}
