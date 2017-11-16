package de.hybris.platform.customerreview.jalo;

import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.customerreview.constants.CustomerReviewConstants;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.jalo.flexiblesearch.FlexibleSearch;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.JaloSession;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

public class CustomerReviewManagerExtension extends CustomerReviewManager {

    private static final Logger LOG = Logger.getLogger(CustomerReviewManagerExtension.class.getName());

    public static CustomerReviewManagerExtension getInstance() {
        ExtensionManager extensionManager = JaloSession.getCurrentSession().getExtensionManager();
        return (CustomerReviewManagerExtension) extensionManager.getExtension("customerreviewextension");
    }

    public String getName()
    {
        return "customerreviewextension";
    }

    public Integer getNumberOfReviewsWithRatingsWithinRange(Product item, Double startRange, Double stopRange) {
        if (startRange == null || startRange.isNaN())
            startRange = CustomerReviewConstants.getInstance().MINRATING;//we dont need to check whether it is smaller than minimal acceptable value

        if (stopRange == null || startRange.isNaN())
            stopRange = CustomerReviewConstants.getInstance().MAXRATING;//same for maximal

        String query = "SELECT count(*) FROM {" + GeneratedCustomerReviewConstants.TC.CUSTOMERREVIEW + "} WHERE {" +
                "product" + "} = ?product" + " AND {rating} <= " + startRange + " AND " + "{" + "rating" + "}" + " >= " + stopRange;
        Map<String, Product> values = Collections.singletonMap("product", item);
        List<Integer> result = FlexibleSearch.getInstance().search(query, values, Collections.singletonList(Integer.class), true, true, 0, -1).getResult();
        return (Integer) result.iterator().next();
    }
}
