package de.hybris.platform.customerreview.impl;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.customerreview.jalo.CustomerReview;
import de.hybris.platform.customerreview.jalo.CustomerReviewManager;
import de.hybris.platform.customerreview.jalo.CustomerReviewManagerExtension;
import de.hybris.platform.customerreview.constants.CustomerReviewConstants;
import org.springframework.beans.factory.annotation.Required;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import org.apache.log4j.Logger;
import java.util.Properties;

public class DefaultCustomerReviewServiceExtension extends DefaultCustomerReviewService{

    private static final Logger LOG = Logger.getLogger(DefaultCustomerReviewServiceExtension.class.getName());
    private Properties prohibitedList;

    public CustomerReviewModel createCustomerReviewWithCurseCheck(Double rating, String headline, String comment, UserModel user, ProductModel product) throws Exception {
        CustomerReview review = CustomerReviewManager.getInstance().createCustomerReview(rating, headline, comment, (User) getModelService().getSource(user), (Product) getModelService().getSource(product));

        //I'm suggesting to change task specification from 0 to MINRATING since this is a configurable value, but if you dont agree disregard next code block, if you do disregard code block after this one
        if (rating < CustomerReviewConstants.getInstance().MINRATING){//suggested task
            LOG.info("user "+user+" provided with product "+product+ " with rating"+rating+" which is less than minimum rating:"+CustomerReviewConstants.getInstance().MINRATING);
            throw new Exception("Invalid rating, please specify rating greater or equal to " + CustomerReviewConstants.getInstance().MINRATING);
        }

        if (rating < 0)//original task
            throw new Exception("Invalid rating, please specify rating greater or equal to 0");

        for (Object key : prohibitedList.keySet()){
            String prohibited = (String) key;
            //is there any reason why we are not checking headline too?
            if (comment.contains(prohibited)) {
                LOG.debug("user "+user+" provided with product "+product+ " with comment "+comment+" which includes word from prohibited words list:"+prohibited);
                throw new Exception("Please refrain from using offensive vocabulary");
            }
            //also, I'd suggest using other methods to check for curse words, this one is trivial to circumvent
        }

        return (CustomerReviewModel)getModelService().get(review);
    }

    public Integer getNumberOfReviewsWithRatingsWithinRange(ProductModel product, Double startRange, Double stopRange)
    {
        return CustomerReviewManagerExtension.getInstance().getNumberOfReviewsWithRatingsWithinRange((Product)getModelService().getSource(product),startRange,stopRange);
    }

    @Required
    public void setProhibitedList(Properties prohibitedList)
    {
        this.prohibitedList = prohibitedList;
    }
}
