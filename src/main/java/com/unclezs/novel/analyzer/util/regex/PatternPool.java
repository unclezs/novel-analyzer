package com.unclezs.novel.analyzer.util.regex;

import com.unclezs.novel.analyzer.common.cache.WeakCache;

import java.util.regex.Pattern;

/**
 * 常用正则表达式集合，更多正则见:https://any86.github.io/any-rule/
 *
 * @author Looly
 */
public class PatternPool {
  /**
   * IP v4
   */
  public static final Pattern IPV4 = Pattern.compile("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");
  /**
   * 分组
   */
  public static final Pattern GROUP_VAR = Pattern.compile("\\$(\\d+)");
  /**
   * 分组
   */
  public static final Pattern HTML_CHARSET = Pattern.compile("<meta.+?(?<default>utf-8|gbk|gb2312)|(?:charset.*?=.*?(?<match>[\\w\\W\\d-]+?)[\"' />]).*?>", Pattern.CASE_INSENSITIVE);
  /**
   * Pattern池
   */
  private static final WeakCache<RegexWithFlag, Pattern> POOL = new WeakCache<>();

  private PatternPool() {
  }

  /**
   * 先从Pattern池中查找正则对应的{@link java.util.regex.Pattern}，找不到则编译正则表达式并入池。
   *
   * @param regex 正则表达式
   * @return {@link java.util.regex.Pattern}
   */
  public static Pattern get(String regex) {
    return get(regex, 0);
  }

  /**
   * 先从Pattern池中查找正则对应的{@link java.util.regex.Pattern}，找不到则编译正则表达式并入池。
   *
   * @param regex 正则表达式
   * @param flags 正则标识位集合 {@link java.util.regex.Pattern}
   * @return {@link java.util.regex.Pattern}
   */
  public static Pattern get(String regex, int flags) {
    final RegexWithFlag regexWithFlag = new RegexWithFlag(regex, flags);

    Pattern pattern = POOL.get(regexWithFlag);
    if (null == pattern) {
      pattern = Pattern.compile(regex, flags);
      POOL.put(regexWithFlag, pattern);
    }
    return pattern;
  }

  /**
   * 移除缓存
   *
   * @param regex 正则
   * @param flags 标识
   * @return 移除的{@link Pattern}，可能为{@code null}
   */
  public static Pattern remove(String regex, int flags) {
    return POOL.remove(new RegexWithFlag(regex, flags));
  }

  /**
   * 清空缓存池
   */
  public static void clear() {
    POOL.clear();
  }


  /**
   * 正则表达式和正则标识位的包装
   *
   * @author Looly
   */
  private static class RegexWithFlag {
    private final String regex;
    private final int flag;

    /**
     * 构造
     *
     * @param regex 正则
     * @param flag  标识
     */
    public RegexWithFlag(String regex, int flag) {
      this.regex = regex;
      this.flag = flag;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + flag;
      result = prime * result + ((regex == null) ? 0 : regex.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      RegexWithFlag other = (RegexWithFlag) obj;
      if (flag != other.flag) {
        return false;
      }
      if (regex == null) {
        return other.regex == null;
      } else {
        return regex.equals(other.regex);
      }
    }

  }
}
