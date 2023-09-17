package com.pool.config.partition;

import com.pool.model.PartitionModel;
import com.pool.repository.CityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class CityRangePartitioner {
    @Autowired
    private CityRepository cityRepository;
    public List<PartitionModel> getPartitionModels(int gridSize){
        List<PartitionModel> partitionModels=new ArrayList<>();
        List<Long> cityIds=cityRepository.getCityIdList();
        if (!cityIds.isEmpty()) {
            if (cityIds.size()<= gridSize) {
                PartitionModel partitionModel= PartitionModel.builder().start(cityIds.get(0)).end(cityIds.get(cityIds.size()-1)).stepNum(1L).build();
                partitionModels.add(partitionModel);
            }else {
                int partitionSize= (int) (Math.ceil(((double) cityIds.size()/(double) gridSize)));
                int sizeTracker=0;
                for (int i = 0; i < gridSize; i++) {
                    PartitionModel partitionModel=new PartitionModel();
                    if (sizeTracker<cityIds.size()) {
                        partitionModel.setStart(cityIds.get(sizeTracker));
                        partitionModel.setStepNum((long)i);
                        if (i<gridSize-1) {
                            partitionModel.setEnd(cityIds.get((sizeTracker + partitionSize) - 1));
                        }else {
                            partitionModel.setEnd(cityIds.get(cityIds.size()-1));
                        }
                        partitionModels.add(partitionModel);
                    }
                    sizeTracker=sizeTracker+partitionSize;
                }
            }
        }
        return  partitionModels;
    }
}
