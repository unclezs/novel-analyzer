package com.unclezs.novel.analyzer.core.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.unclezs.novel.analyzer.core.matcher.matchers.DefaultTextMatcher;
import com.unclezs.novel.analyzer.core.rule.CommonRule;
import com.unclezs.novel.analyzer.core.rule.RuleConstant;
import com.unclezs.novel.analyzer.model.Verifiable;
import com.unclezs.novel.analyzer.request.RequestParams;
import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * 正文规则
 * <pre>
 *     # 最小规则
 *     "content": "xpath://xxxx"
 *     # 完整配置
 *     "content":{
 *        "content": "规则"
 *        "next": "下一页规则"
 *        "enableNext": true
 *     }
 * </pre>
 *
 * @author blog.unclezs.com
 * @since 2021/02/10 11:13
 */
@Data
public class ContentRule implements Verifiable, Serializable, JsonDeserializer<ContentRule> {
  private static final long serialVersionUID = -1810410696732782893L;
  /**
   * 正文翻页
   */
  private Boolean enableNext;
  /**
   * 移除正文中的标题
   */
  private Boolean removeTitle;
  /**
   * 下载延迟
   */
  private Long delayTime;
  /**
   * 繁体转简体
   */
  private Boolean traditionToSimple;
  /**
   * 请求参数
   */
  private RequestParams params;
  /**
   * 正文
   */
  private CommonRule content = CommonRule.create(RuleConstant.TYPE_AUTO, DefaultTextMatcher.DEFAULT_ORDER);
  /**
   * 正文下一页规则（存在则会匹配下一页）
   */
  private CommonRule next = CommonRule.create("xpath", RuleConstant.NEXT_PAGE_RULE);

  /**
   * 正文规则是否有效
   *
   * @return /
   */
  public static boolean isEffective(ContentRule rule) {
    return rule != null && CommonRule.isEffective(rule.getContent());
  }

  /**
   * 是否允许正文翻页 规则必须存在
   *
   * @return /
   */
  public boolean isAllowNextPage() {
    return Boolean.TRUE.equals(enableNext) && CommonRule.isEffective(next);
  }

  /**
   * 正文可以不填写规则 有系统提供的
   *
   * @return true
   */
  @Override
  public boolean isEffective() {
    return true;
  }

  /**
   * 自定义反序列化正文规则
   * 支持直接填写string规则
   * <pre>
   *     content: "xpath://xxxx"
   *     content:{
   *        content: "规则"
   *        next: "下一页规则"
   *        enableNext: true
   *     }
   * </pre>
   *
   * @param json    JSON
   * @param typeOfT 类型
   * @param context 上下文
   * @return 结果
   * @throws JsonParseException 解析异常
   */
  @Override
  public ContentRule deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    ContentRule contentRule = new ContentRule();
    if (json.isJsonObject()) {
      JsonObject jsonObject = json.getAsJsonObject();
      JsonElement enableNextElement = jsonObject.get("enableNext");
      // 启用下一页
      if (enableNextElement != null) {
        contentRule.setEnableNext(enableNextElement.getAsBoolean());
      }
      // 繁体转简体
      JsonElement traditionToSimpleElement = jsonObject.get("traditionToSimple");
      if (traditionToSimpleElement != null) {
        contentRule.setTraditionToSimple(traditionToSimpleElement.getAsBoolean());
      }
      // 移除标题
      JsonElement removeTitleElement = jsonObject.get("removeTitle");
      if (removeTitleElement != null) {
        contentRule.setRemoveTitle(removeTitleElement.getAsBoolean());
      }
      // 下载延迟
      JsonElement delayTimeElement = jsonObject.get("delayTime");
      if (delayTimeElement != null) {
        contentRule.setDelayTime(delayTimeElement.getAsLong());
      }
      // 内容规则
      JsonElement contentElement = jsonObject.get("content");
      if (contentElement != null) {
        contentRule.setContent(context.deserialize(contentElement, CommonRule.class));
      }
      // 下一页规则
      JsonElement nextElement = jsonObject.get("next");
      if (nextElement != null) {
        contentRule.setNext(context.deserialize(nextElement, CommonRule.class));
      }
      // 请求参数
      JsonElement paramsElement = jsonObject.get("params");
      if (paramsElement != null) {
        contentRule.setParams(context.deserialize(paramsElement, RequestParams.class));
      }
    } else {
      // 直接填写正文规则
      contentRule.setContent(CommonRule.create(json.getAsString()));
    }
    return contentRule;
  }
}
