import com.google.testing.compile.JavaFileObjects;
import com.ap.MyProcessor;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class ProcessorTest {

    @Test
    public void testGenerateCode() {

        JavaFileObject source = JavaFileObjects.forSourceString("test.Test", "" +
                "package test;\n" +
                "import com.mine.annotation.BackupInterceptor;\n" +
                "import com.mine.IInterceptor;\n" +
                "public class Test implements IInterceptor {\n" +
                "    @BackupInterceptor(index = 1)\n" +
                "    public Test() {} \n" +
                "}");

        JavaFileObject bindResource = JavaFileObjects.forSourceString("test/InterceptorsFactory", "" +
                "package test;\n" +
                "\n" +
                "import com.mine.IInterceptor;\n" +
                "import java.util.ArrayList;\n" +
                "import java.util.List;\n" +
                "\n" +
                "public class InterceptorsFactory {\n" +
                "  private static InterceptorsFactory instance;\n" +
                "\n" +
                "  public static InterceptorsFactory getInstance() {\n" +
                "    if (null == instance) {\n" +
                "      instance = new InterceptorsFactory();\n" +
                "    }\n" +
                "    return instance;\n" +
                "  }\n" +
                "  private List<IInterceptor> getInterceptors() {\n" +
                "   List<IInterceptor> interceptors = new ArrayList();\n" +
                "    interceptors.add(generic_Test());\n" +
                "    return interceptors;\n" +
                "  }\n" +
                "\n" +
                "  public Test generic_Test() {\n" +
                "    return new Test();\n" +
                "  }\n" +
                "}");

        assertAbout(javaSource()).that(source)
                .withCompilerOptions("-Xlint:-processing")
                .processedWith(new MyProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(bindResource);
    }
}
