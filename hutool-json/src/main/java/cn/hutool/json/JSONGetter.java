package cn.hutool.json;

import cn.hutool.core.convert.ConvertException;
import cn.hutool.core.lang.getter.OptNullBasicTypeFromObjectGetter;
import cn.hutool.core.util.ObjUtil;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 用于JSON的Getter类，提供各种类型的Getter方法
 *
 * @param <K> Key类型
 * @author Looly
 */
public interface JSONGetter<K> extends OptNullBasicTypeFromObjectGetter<K> {

	/**
	 * 获取JSON配置
	 *
	 * @return {@link JSONConfig}
	 * @since 5.3.0
	 */
	JSONConfig getConfig();

	/**
	 * key对应值是否为{@code null}或无此key
	 *
	 * @param key 键
	 * @return true 无此key或值为{@code null}返回{@code false}，其它返回{@code true}
	 */
	default boolean isNull(final K key) {
		return ObjUtil.isNull(this.getObj(key));
	}

	/**
	 * 获取字符串类型值，并转义不可见字符，如'\n'换行符会被转义为字符串"\n"
	 *
	 * @param key 键
	 * @return 字符串类型值
	 * @since 4.2.2
	 */
	default String getStrEscaped(final K key) {
		return getStrEscaped(key, null);
	}

	/**
	 * 获取字符串类型值，并转义不可见字符，如'\n'换行符会被转义为字符串"\n"
	 *
	 * @param key          键
	 * @param defaultValue 默认值
	 * @return 字符串类型值
	 * @since 4.2.2
	 */
	default String getStrEscaped(final K key, final String defaultValue) {
		return InternalJSONUtil.escape(getStr(key, defaultValue));
	}

	/**
	 * 获得JSONArray对象<br>
	 * 如果值为其它类型对象，尝试转换为{@link JSONArray}返回，否则抛出异常
	 *
	 * @param key KEY
	 * @return JSONArray对象，如果值为{@code null}，返回{@code null}，非JSONArray类型，尝试转换，转换失败抛出异常
	 */
	default JSONArray getJSONArray(final K key) {
		final Object object = this.getObj(key);
		if (ObjUtil.isNull(object)) {
			return null;
		}

		if (object instanceof JSON) {
			return (JSONArray) object;
		}
		return new JSONArray(object, getConfig());
	}

	/**
	 * 获得JSONObject对象<br>
	 * 如果值为其它类型对象，尝试转换为{@link JSONObject}返回，否则抛出异常
	 *
	 * @param key KEY
	 * @return JSONObject对象，如果值为{@code null}，返回{@code null}，非JSONObject类型，尝试转换，转换失败抛出异常
	 */
	default JSONObject getJSONObject(final K key) {
		final Object object = this.getObj(key);
		if (ObjUtil.isNull(object)) {
			return null;
		}

		if (object instanceof JSON) {
			return (JSONObject) object;
		}
		return new JSONObject(object, getConfig());
	}

	/**
	 * 从JSON中直接获取Bean对象<br>
	 * 先获取JSONObject对象，然后转为Bean对象
	 *
	 * @param <T>      Bean类型
	 * @param key      KEY
	 * @param beanType Bean类型
	 * @return Bean对象，如果值为null或者非JSONObject类型，返回null
	 * @since 3.1.1
	 */
	default <T> T getBean(final K key, final Class<T> beanType) {
		final JSONObject obj = getJSONObject(key);
		return (null == obj) ? null : obj.toBean(beanType);
	}

	/**
	 * 从JSON中直接获取Bean的List列表<br>
	 * 先获取JSONArray对象，然后转为Bean的List
	 *
	 * @param <T>      Bean类型
	 * @param key      KEY
	 * @param beanType Bean类型
	 * @return Bean的List，如果值为null或者非JSONObject类型，返回null
	 * @since 5.7.20
	 */
	default <T> List<T> getBeanList(final K key, final Class<T> beanType) {
		final JSONArray jsonArray = getJSONArray(key);
		return (null == jsonArray) ? null : jsonArray.toList(beanType);
	}

	@Override
	default Date getDate(final K key, final Date defaultValue) {
		return get(key, Date.class);
	}

	/**
	 * 获取{@link LocalDateTime}类型值
	 *
	 * @param key          键
	 * @param defaultValue 默认值
	 * @return {@link LocalDateTime}
	 * @since 5.7.7
	 */
	default LocalDateTime getLocalDateTime(final K key, final LocalDateTime defaultValue) {
		return ObjUtil.defaultIfNull(get(key, LocalDateTime.class), defaultValue);
	}

	/**
	 * 获取byte[]数据
	 *
	 * @param key 键
	 * @return 值
	 */
	default byte[] getBytes(final K key) {
		return get(key, byte[].class);
	}

	/**
	 * 获取指定类型的对象
	 *
	 * @param <T>         获取的对象类型
	 * @param key         键
	 * @param type        获取对象类型
	 * @return 对象
	 * @throws ConvertException 转换异常
	 * @since 3.0.8
	 */
	@SuppressWarnings("unchecked")
	default <T> T get(final K key, final Class<T> type) throws ConvertException {
		final Object value = this.getObj(key);
		if (ObjUtil.isNull(value)) {
			return null;
		}

		return (T) getConfig().getConverter().convert(type, value);
	}
}
