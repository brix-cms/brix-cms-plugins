/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package brix.plugin.file.admin.add.upload;

import java.io.Serializable;

/**
 * @author wickeria at gmail.com
 */
public class UploadFileConfiguration implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer resultImageMaxSize; // ve smyslu max rozliseni
	private boolean allowResultImageMaxSizeEdit;

	public UploadFileConfiguration(Integer resultImageMaxSize, boolean allowResultImageMaxSizeEdit) {
		super();
		this.resultImageMaxSize = resultImageMaxSize;
		this.allowResultImageMaxSizeEdit = allowResultImageMaxSizeEdit;
	}

	public Integer getResultImageMaxSize() {
		return resultImageMaxSize;
	}

	public void setResultImageMaxSize(Integer resultImageMaxSize) {
		this.resultImageMaxSize = resultImageMaxSize;
	}

	public boolean isAllowResultImageMaxSizeEdit() {
		return allowResultImageMaxSizeEdit;
	}

	public void setAllowResultImageMaxSizeEdit(boolean allowResultImageMaxSizeEdit) {
		this.allowResultImageMaxSizeEdit = allowResultImageMaxSizeEdit;
	}

}
