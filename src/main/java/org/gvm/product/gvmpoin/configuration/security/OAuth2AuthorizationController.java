package org.gvm.product.gvmpoin.configuration.security;

import org.gvm.product.gvmpoin.util.TemplateLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.PropertyPlaceholderHelper.PlaceholderResolver;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@SessionAttributes("authorizationRequest")
public class OAuth2AuthorizationController {

  private static final String _CSRF = "_csrf";
  private static final String SCOPES = "scopes";
  private static final String FORMAT_SCOPES = "%scopes%";
  private static final String FORMAT_CSRF = "%csrf%";
  private static final String REDIRECT_URI = "redirect_uri";
  private static final String CLIENT_ID = "client_id";
  private static final String HTML_CSRF = "<input type='hidden' name='${_csrf.parameterName}' "
      + "value='${_csrf.token}' />";
  private static final String HTML_SCOPE = "<input type='hidden' name='%key%' value='true'/>";

  @Autowired
  private TemplateLoader templateLoader;

  /**
   * @param model .
   * @return (ModelAndView) access confirmation
   * @throws Exception throws Exception
   */
  @GetMapping("/oauth/confirm_access")
  public ModelAndView getAccessConfirmation(Map<String, Object> model,
      HttpServletRequest request) throws Exception {
    String authorizationFormTemplate = "templates/ps_oauth_authorization_form.html";
    String template = templateLoader.load(authorizationFormTemplate);

    template = template.replace(FORMAT_SCOPES, createScopes(model, request));

    if (model.containsKey(_CSRF) || request.getAttribute(_CSRF) != null) {
      template = template.replace(FORMAT_CSRF, HTML_CSRF);
    } else {
      template = template.replace(FORMAT_CSRF, "");
    }

    if (request.getAttribute(_CSRF) != null) {
      model.put(_CSRF, request.getAttribute(_CSRF));
    }

    model.put(REDIRECT_URI, request.getAttribute(REDIRECT_URI));
    model.put(CLIENT_ID, getFormattedClientId(request));

    return new ModelAndView(new SpelView(template), model);
  }

  private String getFormattedClientId(HttpServletRequest request) {
    String clientId = (String) request.getAttribute(CLIENT_ID);
    return clientId.substring(0, 1).toUpperCase() + clientId.substring(1);
  }

  private CharSequence createScopes(Map<String, Object> model, HttpServletRequest request) {
    @SuppressWarnings("unchecked")
    Map<String, String> scopes = (Map<String, String>) (model.containsKey(SCOPES)
        ? model.get(SCOPES) : request.getAttribute(SCOPES));

    StringBuilder builder = new StringBuilder();
    for (String scope : scopes.keySet()) {
      String value = HTML_SCOPE.replace("%key%", scope);
      builder.append(value);
    }

    return builder.toString();
  }

  private static class SpelView implements View {

    private final String template;
    private final String prefix;
    private final SpelExpressionParser parser = new SpelExpressionParser();
    private final StandardEvaluationContext context = new StandardEvaluationContext();
    private PlaceholderResolver resolver;

    SpelView(String template) {
      this.template = template;
      this.prefix = new RandomValueStringGenerator().generate() + "{";
      this.context.addPropertyAccessor(new MapAccessor());
      this.resolver = name -> {
        Expression expression = parser.parseExpression(name);
        Object value = expression.getValue(context);
        return value == null ? null : value.toString();
      };
    }

    public String getContentType() {
      return "text/html";
    }

    public void render(Map<String, ?> model, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
      Map<String, Object> map = new HashMap<>(model);
      String path = ServletUriComponentsBuilder.fromContextPath(request).build().getPath();
      map.put("path", path == null ? "" : path);
      context.setRootObject(map);

      String maskedTemplate = template.replace("${", prefix);
      PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper(prefix, "}");
      String result = helper.replacePlaceholders(maskedTemplate, resolver);
      result = result.replace(prefix, "${");

      response.setContentType(getContentType());
      response.getWriter().append(result);
    }
  }
}
