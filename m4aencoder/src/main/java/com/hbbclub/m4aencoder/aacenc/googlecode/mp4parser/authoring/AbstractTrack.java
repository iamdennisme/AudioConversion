/*
 * Copyright 2012 Sebastian Annies, Hamburg
 *
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an AS IS BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hbbclub.m4aencoder.aacenc.googlecode.mp4parser.authoring;

/**
 *
 */
public abstract class AbstractTrack implements Track {
    private boolean enabled = true;
    private boolean inMovie = true;
    private boolean inPreview = true;
    private boolean inPoster = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isInMovie() {
        return inMovie;
    }

    public void setInMovie(boolean inMovie) {
        this.inMovie = inMovie;
    }

    public boolean isInPreview() {
        return inPreview;
    }

    public void setInPreview(boolean inPreview) {
        this.inPreview = inPreview;
    }

    public boolean isInPoster() {
        return inPoster;
    }

    public void setInPoster(boolean inPoster) {
        this.inPoster = inPoster;
    }

}