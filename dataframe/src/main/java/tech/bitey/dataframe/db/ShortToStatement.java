/*
 * Copyright 2021 biteytech@protonmail.com
 * 
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

package tech.bitey.dataframe.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import tech.bitey.dataframe.ShortColumn;

public enum ShortToStatement implements IToPreparedStatement<ShortColumn> {
	SHORT_TO_SHORT {

		@Override
		public void set(ShortColumn column, int rowIndex, PreparedStatement ps, int paramIndex) throws SQLException {

			if (column.isNull(rowIndex))
				ps.setNull(paramIndex, java.sql.Types.INTEGER);
			else {
				short value = column.getShort(rowIndex);

				ps.setShort(paramIndex, value);
			}
		}
	};
}
