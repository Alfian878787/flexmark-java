/*
 * Copyright (c) 2015-2016 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
 *
 * This code is private property of the copyright holder and cannot be used without
 * having obtained a license or prior written permission of the of the copyright holder.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package com.vladsch.flexmark.ext.escaped.character;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.spec.SpecExample;
import com.vladsch.flexmark.test.ComboSpecTestCase;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.junit.runners.Parameterized;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComboEscapedCharacterSpecTest extends ComboSpecTestCase {
    private static final String SPEC_RESOURCE = "/ext_escaped_character_ast_spec.md";
    private static final DataHolder OPTIONS = new MutableDataSet()
            .set(HtmlRenderer.INDENT_SIZE, 2)
            //.set(HtmlRenderer.PERCENT_ENCODE_URLS, true)
            .set(Parser.EXTENSIONS, Collections.singleton(EscapedCharacterExtension.create()));

    private static final Map<String, DataHolder> optionsMap = new HashMap<>();
    static {
        optionsMap.put("src-pos", new MutableDataSet().set(HtmlRenderer.SOURCE_POSITION_ATTRIBUTE, "md-pos"));
        //optionsMap.put("option1", new MutableDataSet().set(EscapedCharacterExtension.ESCAPED_CHARACTER_OPTION1, true));
    }

    private static final Parser PARSER = Parser.builder(OPTIONS).build();
    // The spec says URL-escaping is optional, but the examples assume that it's enabled.
    private static final HtmlRenderer RENDERER = HtmlRenderer.builder(OPTIONS).build();

    private static DataHolder optionsSet(String optionSet) {
        if (optionSet == null) return null;
        return optionsMap.get(optionSet);
    }

    public ComboEscapedCharacterSpecTest(SpecExample example) {
        super(example);
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<Object[]> data() {
        return getTestData(SPEC_RESOURCE);
    }

    @Override
    public DataHolder options(String optionSet) {
        return optionsSet(optionSet);
    }

    @Override
    public String getSpecResourceName() {
        return SPEC_RESOURCE;
    }

    @Override
    public Parser parser() {
        return PARSER;
    }

    @Override
    public HtmlRenderer renderer() {
        return RENDERER;
    }
}
