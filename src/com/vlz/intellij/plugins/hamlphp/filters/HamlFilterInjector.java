package com.vlz.intellij.plugins.hamlphp.filters;

/**
 * Created by VLZ on 26.11.2014.
 */
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.util.containers.HashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.haml.psi.HAMLTokenTypes;
import org.jetbrains.plugins.haml.psi.impl.HAMLTagImpl;

public class HamlFilterInjector
        implements MultiHostInjector
{
    private static final Map<String, String> HAML_FILTER_TO_LANGUAGE_ID_MAP = new HashMap();

    static
    {
        HAML_FILTER_TO_LANGUAGE_ID_MAP.put("ruby", "ruby");
        HAML_FILTER_TO_LANGUAGE_ID_MAP.put("javascript", "JavaScript");
        HAML_FILTER_TO_LANGUAGE_ID_MAP.put("css", "CSS");
        HAML_FILTER_TO_LANGUAGE_ID_MAP.put("sass", "SASS");
        HAML_FILTER_TO_LANGUAGE_ID_MAP.put("scss", "SCSS");
        HAML_FILTER_TO_LANGUAGE_ID_MAP.put("less", "LESS");
        HAML_FILTER_TO_LANGUAGE_ID_MAP.put("styl", "Stylus");
        HAML_FILTER_TO_LANGUAGE_ID_MAP.put("coffee", "CoffeeScript");
        HAML_FILTER_TO_LANGUAGE_ID_MAP.put("erb", "RHTML");
        HAML_FILTER_TO_LANGUAGE_ID_MAP.put("php", "PHP");
    }

    public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement element)
    {
        if (registrar == null) {
            throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", new Object[] { "registrar", "org/jetbrains/plugins/haml/filters/HamlFilterInjector", "getLanguagesToInject" }));
        }
        if (element == null) {
            throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", new Object[] { "element", "org/jetbrains/plugins/haml/filters/HamlFilterInjector", "getLanguagesToInject" }));
        }
        if ((element instanceof HAMLTagImpl))
        {
            ASTNode firstChild = element.getNode().getFirstChildNode();
            if (firstChild.getElementType() == HAMLTokenTypes.INDENT) {
                firstChild = firstChild.getTreeNext();
            }
            if ((firstChild != null) && (firstChild.getElementType() == HAMLTokenTypes.FILTER))
            {
                ASTNode possibleFilterContent = firstChild.getTreeNext();
                if (possibleFilterContent.getElementType() == HAMLTokenTypes.FILTER_CONTENT)
                {
                    String languageName = firstChild.getText();
                    if (languageName.startsWith(":")) {
                        languageName = languageName.substring(1);
                    }
                    if (languageName.equals("blabla-php")) {
                        languageName = "php";
                    }
                    String languageId = (String)HAML_FILTER_TO_LANGUAGE_ID_MAP.get(languageName);
                    if (languageId == null) {
                        return;
                    }
                    Language filterLanguage = Language.findLanguageByID(languageId);
                    if (filterLanguage != null)
                    {
                        int injectionOffset = possibleFilterContent.getStartOffset() - ((HAMLTagImpl)element).getStartOffset();
                        TextRange range = new TextRange(injectionOffset, injectionOffset + possibleFilterContent.getTextLength());
                        registrar.startInjecting(filterLanguage).addPlace(null, null, (PsiLanguageInjectionHost)element, range).doneInjecting();
                    }
                }
            }
        }
    }

    @NotNull
    public List<? extends Class<? extends PsiElement>> elementsToInjectIn()
    {
        List tmp12_9 = Arrays.asList(new Class[] { HAMLTagImpl.class });
        if (tmp12_9 == null) {
            throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", new Object[] { "org/jetbrains/plugins/haml/filters/HamlFilterInjector", "elementsToInjectIn" }));
        }
        return tmp12_9;
    }
}

