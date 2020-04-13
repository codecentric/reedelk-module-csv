package com.reedelk.csv.component;

import com.reedelk.runtime.api.annotation.DisplayName;
import org.apache.commons.csv.CSVFormat;

public enum Format {

    @DisplayName("Default")
    DEFAULT {
        @Override
        public CSVFormat format() {
            return CSVFormat.DEFAULT;
        }
    },

    @DisplayName("Excel")
    EXCEL {
        @Override
        public CSVFormat format() {
            return CSVFormat.EXCEL;
        }
    },

    @DisplayName("Informix Unload")
    INFORMIX_UNLOAD {
        @Override
        public CSVFormat format() {
            return CSVFormat.INFORMIX_UNLOAD;
        }
    },

    @DisplayName("Informix Unload CSV")
    INFORMIX_UNLOAD_CSV {
        @Override
        public CSVFormat format() {
            return CSVFormat.INFORMIX_UNLOAD_CSV;
        }
    },

    @DisplayName("MongoDB CSV")
    MONGODB_CSV {
        @Override
        public CSVFormat format() {
            return CSVFormat.MONGODB_CSV;
        }
    },

    @DisplayName("MongoDB TSV")
    MONGODB_TSV {
        @Override
        public CSVFormat format() {
            return CSVFormat.MONGODB_TSV;
        }
    },

    @DisplayName("MySQL")
    MYSQL {
        @Override
        public CSVFormat format() {
            return CSVFormat.MYSQL;
        }
    },

    @DisplayName("Oracle")
    ORACLE {
        @Override
        public CSVFormat format() {
            return CSVFormat.ORACLE;
        }
    },

    @DisplayName("PostgreSQL CSV")
    POSTGRESQL_CSV {
        @Override
        public CSVFormat format() {
            return CSVFormat.POSTGRESQL_CSV;
        }
    },

    @DisplayName("PostgreSQL TEXT")
    POSTGRESQL_TEXT {
        @Override
        public CSVFormat format() {
            return CSVFormat.POSTGRESQL_TEXT;
        }
    },

    @DisplayName("RFC4180")
    RFC4180 {
        @Override
        public CSVFormat format() {
            return CSVFormat.RFC4180;
        }
    };

    public abstract CSVFormat format();

}
