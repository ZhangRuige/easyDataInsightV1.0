package hadoop;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.zhongyitech.edi.product.parse.ProductParametersHtml2Map;




public class ProductParametersHtml2MapTest {
	@Test	
	public void test(String htmlstr){
		Map<String, String> map = ProductParametersHtml2Map.doParse(htmlstr);
		Assert.assertTrue(map.size()>0);
	}
}
