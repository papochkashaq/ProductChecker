package org.example.productchecker.checker;

import org.example.productchecker.exception.PageFetchException;
import org.example.productchecker.model.CheckResult;

public interface ProductChecker {

    CheckResult checkProduct(String url) throws PageFetchException;
}
