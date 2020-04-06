#include "algorithm-taxonomy.hpp"
#include "../include-opencv.hpp"
#include <fmo/processing.hpp>
#include <iostream>
#include <fmo/assert.hpp>

namespace fmo {
    void registerTaxonomyV1() {
        Algorithm::registerFactory(
            "taxonomy-v1", [](const Algorithm::Config& config, Format format, Dims dims) {
                return std::unique_ptr<Algorithm>(new TaxonomyV1(config, format, dims));
            });
    }

    TaxonomyV1::TaxonomyV1(const Config& cfg, Format format, Dims dims)
        : mCfg(cfg), mSourceLevel{{format, dims}, 0}, mDiff(cfg.diff) {
    }

    void TaxonomyV1::setInputSwap(Image& in) {
        swapAndSubsampleInput(in);
        computeBinDiff();
        findComponents();
        processComponents();
    }

    void TaxonomyV1::swapAndSubsampleInput(Image& in) {
        if (in.format() != mSourceLevel.image.format()) {
            throw std::runtime_error("setInputSwap(): bad format");
        }

        if (in.dims() != mSourceLevel.image.dims()) {
            throw std::runtime_error("setInputSwap(): bad dimensions");
        }

        mSourceLevel.image.swap(in);
        mSourceLevel.frameNum++;

        if (mSourceLevel.image.format() == Format::YUV420SP) {
            int pixelSizeLog2 = 0;
            Image* input = &mSourceLevel.image;

            for (; input->dims().height > mCfg.maxImageHeight; pixelSizeLog2++) {
                if (int(mCache.subsampled.size()) == pixelSizeLog2) {
                    mCache.subsampled.emplace_back(new Image);
                }
                auto& next = *mCache.subsampled[pixelSizeLog2];
                mSubsampler(*input, next);
                input = &next;
            }
            mCache.image = *mCache.subsampled[mCache.subsampled.size()-1];
            mProcessingLevel.scale = (1.0/pow(2, pixelSizeLog2));

            if(in.do_flip)
                cv::flip(mCache.subsampled[mCache.subsampled.size()-1]->wrap(), mCache.image.wrap(), 1);
            *mCache.subsampled[mCache.subsampled.size()-1] = mCache.image;

            cv::cvtColor(mCache.subsampled[mCache.subsampled.size()-1]->wrap(), mCache.image.wrap(), cv::COLOR_YUV2BGR);
        } else {
            // resize an image to exact height
            mProcessingLevel.scale = (float) mCfg.imageHeight / in.dims().height;
            subsample_resize(mSourceLevel.image, mCache.image, mProcessingLevel.scale);
        }

        if(mSourceLevel.frameNum <= 1) {
            auto& level = mProcessingLevel;
            level.dims = in.dims();
            level.newDims = mCache.image.dims();
            level.binDiff.resize(Format::GRAY, level.newDims);
            level.binDiff.wrap().setTo(0);

            level.diffAcc.resize(Format::GRAY, level.newDims);
            level.diffAcc.wrap().setTo(0);

            level.diff.resize(mCache.image.format(), level.newDims);
            level.diff.wrap().setTo(0);

            level.background.resize(mCache.image.format(), level.newDims);
            level.background.wrap().setTo(0);

            level.labels.resize(Format::INT32, level.newDims);
            level.distTran.resize(Format::FLOAT, level.newDims);
            level.localMaxima.resize(Format::GRAY, level.newDims);

            mCache.distTranReverse.resize(Format::FLOAT, level.newDims);
            mCache.distTranGray.resize(Format::GRAY, level.newDims);
            mCache.distTranBGR.resize(mCache.image.format(), level.newDims);
            mCache.visualized.resize(mCache.image.format(), level.newDims);
            mCache.visualizedFull.resize(mCache.image.format(), level.dims);
            mCache.objectsMask.resize(mCache.image.format(), level.dims);
            mCache.ones.resize(Format::GRAY, level.newDims);
            mCache.ones.wrap().setTo(1);
        }

        // swap the product of decimation into the processing level
        mProcessingLevel.inputs[3].swap(mProcessingLevel.inputs[2]);
        mProcessingLevel.inputs[2].swap(mProcessingLevel.inputs[1]);
        mProcessingLevel.inputs[1].swap(mProcessingLevel.inputs[0]);
        mProcessingLevel.inputs[0].swap(mCache.image);
    }

    void TaxonomyV1::computeBinDiff() {
        auto& level = mProcessingLevel;

        if (mSourceLevel.frameNum < 5) {
            // initial frames
            return;
        }

        fmo::median5(level.inputs[0], level.inputs[1], level.inputs[2],
            level.inputs[3], level.background, level.background);


//////      level.diff.wrap() = abs(level.inputs[0].wrap() - level.background.wrap());
//////        cv::transform(level.diff.wrap(), level.diffAcc.wrap(), cv::Matx13f(1,1,1));
        level.binDiffPrev.swap(level.binDiff);
        mDiff(level.inputs[0], level.background, level.binDiff);
    }
}
