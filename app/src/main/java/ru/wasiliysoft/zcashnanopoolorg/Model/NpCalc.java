package ru.wasiliysoft.zcashnanopoolorg.Model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import static ru.wasiliysoft.zcashnanopoolorg.Helper.getRoundEarnings;

public class NpCalc extends NpGeneral {

    public class Resp {
        private Data data;

        public Data getData() {
            return data;
        }
    }

    public class Data {

        @SerializedName("minute")
        @Expose
        private Minute minute;
        @SerializedName("hour")
        @Expose
        private Hour hour;
        @SerializedName("day")
        @Expose
        private Day day;
        @SerializedName("week")
        @Expose
        private Week week;
        @SerializedName("month")
        @Expose
        private Month month;

        public Minute getMinute() {
            return minute;
        }

        public Hour getHour() {
            return hour;
        }

        public Day getDay() {
            return day;
        }

        public Week getWeek() {
            return week;
        }

        public Month getMonth() {
            return month;
        }


    }

    public class Earnings {

        @SerializedName("coins")
        @Expose
        private Double coins;
        @SerializedName("dollars")
        @Expose
        private Double dollars;
        @SerializedName("yuan")
        @Expose
        private Double yuan;
        @SerializedName("euros")
        @Expose
        private Double euros;
        @SerializedName("rubles")
        @Expose
        private Double rubles;
        @SerializedName("bitcoins")
        @Expose
        private Double bitcoins;

        public String getCoins() {
            return getRoundEarnings(coins);
        }

        public String getDollars() {
            return getRoundEarnings(dollars);
        }

        public String getYuan() {
            return getRoundEarnings(yuan);
        }

        public String getEuros() {
            return getRoundEarnings(euros);
        }

        public String getRubles() {
            return getRoundEarnings(rubles);
        }

        public String getBitcoins() {
            return getRoundEarnings(bitcoins);
        }

        public String getPeriod() {
            return "";
        }


    }


    public class Day extends Earnings {
        public String getPeriod() {
            return "Day";
        }
    }


    public class Hour extends Earnings {
        public String getPeriod() {
            return "Hour";
        }
    }


    public class Minute extends Earnings {
        public String getPeriod() {
            return "Minute";
        }

    }


    public class Month extends Earnings {

        public String getPeriod() {
            return "Month";
        }

    }


    public class Week extends Earnings {

        public String getPeriod() {
            return "Week";
        }

    }
}
