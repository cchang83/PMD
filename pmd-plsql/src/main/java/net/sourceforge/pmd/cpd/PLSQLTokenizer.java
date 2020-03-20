/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Properties;

import org.apache.commons.io.input.CharSequenceReader;

import net.sourceforge.pmd.cpd.internal.JavaCCTokenizer;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.GenericToken;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLTokenKinds;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLTokenManager;
import net.sourceforge.pmd.util.IOUtil;

public class PLSQLTokenizer extends JavaCCTokenizer {
    // This is actually useless, the comments are special tokens, never taken into account by CPD
    @Deprecated
    public static final String IGNORE_COMMENTS = "ignore_comments";
    public static final String IGNORE_IDENTIFIERS = "ignore_identifiers";
    public static final String IGNORE_LITERALS = "ignore_literals";

    private boolean ignoreIdentifiers;
    private boolean ignoreLiterals;

    public void setProperties(Properties properties) {
        /*
         * The Tokenizer is derived from PLDoc, in which comments are very
         * important When looking for duplication, we are probably not
         * interested in comment variation, so we shall default ignoreComments
         * to true
         */
        ignoreIdentifiers = Boolean.parseBoolean(properties.getProperty(IGNORE_IDENTIFIERS, "false"));
        ignoreLiterals = Boolean.parseBoolean(properties.getProperty(IGNORE_LITERALS, "false"));
    }

    @Deprecated
    public void setIgnoreComments(boolean ignore) {
        // This is actually useless, the comments are special tokens, never taken into account by CPD
    }

    public void setIgnoreLiterals(boolean ignore) {
        this.ignoreLiterals = ignore;
    }

    public void setIgnoreIdentifiers(boolean ignore) {
        this.ignoreIdentifiers = ignore;
    }

    @Override
    protected TokenEntry processToken(Tokens tokenEntries, GenericToken currentToken, String fileName) {
        String image = currentToken.getImage();

        JavaccToken plsqlToken = (JavaccToken) currentToken;

        if (ignoreIdentifiers && plsqlToken.kind == PLSQLTokenKinds.IDENTIFIER) {
            image = String.valueOf(plsqlToken.kind);
        }

        if (ignoreLiterals && (plsqlToken.kind == PLSQLTokenKinds.UNSIGNED_NUMERIC_LITERAL
                || plsqlToken.kind == PLSQLTokenKinds.FLOAT_LITERAL
                || plsqlToken.kind == PLSQLTokenKinds.INTEGER_LITERAL
                || plsqlToken.kind == PLSQLTokenKinds.CHARACTER_LITERAL
                || plsqlToken.kind == PLSQLTokenKinds.STRING_LITERAL
                || plsqlToken.kind == PLSQLTokenKinds.QUOTED_LITERAL)) {
            image = String.valueOf(plsqlToken.kind);
        }

        return new TokenEntry(image, fileName, currentToken.getBeginLine(),
                currentToken.getBeginColumn(), currentToken.getEndColumn());
    }

    @Override
    protected TokenManager getLexerForSource(SourceCode sourceCode) {
        return new PLSQLTokenManager(IOUtil.skipBOM(new CharSequenceReader(sourceCode.getCodeBuffer())));
    }
}
