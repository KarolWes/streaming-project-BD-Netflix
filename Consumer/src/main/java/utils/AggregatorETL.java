package utils;

import models.CombinedData;
import models.EtlAgg;
import models.TEMPEtl;
import org.apache.flink.api.common.functions.AggregateFunction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class AggregatorETL implements AggregateFunction<CombinedData, TEMPEtl, EtlAgg> {

    @Override
    public TEMPEtl createAccumulator() {
        return new TEMPEtl();
    }

    @Override
    public TEMPEtl add(CombinedData combinedData, TEMPEtl tempEtl) {
        tempEtl.setTitle(combinedData.getTitle());
        tempEtl.setMovieId(combinedData.getMovieId());
        tempEtl.setDate(combinedData.getDate());
        tempEtl.setRateCount(tempEtl.getRateCount()+1);
        tempEtl.setRateSum(combinedData.getRate()+tempEtl.getRateSum());
        tempEtl.addReviewer(combinedData.getUserId());
        return tempEtl;
    }

    @Override
    public EtlAgg getResult(TEMPEtl tempEtl) {
        EtlAgg e = new EtlAgg();
        e.setTitle(tempEtl.getTitle());
        e.setFilmId(tempEtl.getMovieId());
        e.setDate(tempEtl.getDate());
        e.setRateCount(tempEtl.getRateCount());
        e.setRateSum(tempEtl.getRateSum());
        e.setReviewerCount((long) new HashSet<>(tempEtl.getReviewers()).size());
        return e;
    }

    @Override
    public TEMPEtl merge(TEMPEtl tempEtl, TEMPEtl acc1) {
        TEMPEtl te = new TEMPEtl();
        te.setTitle(tempEtl.getTitle());
        te.setDate(tempEtl.getDate());
        te.setMovieId(tempEtl.getMovieId());
        te.setRateCount(tempEtl.getRateCount()+ acc1.getRateCount());
        te.setRateSum(tempEtl.getRateSum()+ acc1.getRateSum());
        List<Integer> reviewers = tempEtl.getReviewers();
        reviewers.addAll(acc1.getReviewers());
        reviewers = new ArrayList<>(new HashSet<>(reviewers));
        te.setReviewers(reviewers);
        return te;
    }
}
