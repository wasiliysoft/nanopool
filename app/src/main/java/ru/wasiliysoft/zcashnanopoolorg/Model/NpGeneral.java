package ru.wasiliysoft.zcashnanopoolorg.Model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import static ru.wasiliysoft.zcashnanopoolorg.Helper.getRoundEarnings;

public class NpGeneral {
    public class Resp {

        @SerializedName("status")
        @Expose
        private Boolean status;

        @SerializedName("data")
        @Expose
        private Data data;

        @SerializedName("error")
        @Expose
        private String error;

        public String getError() {
            return error;
        }

        public Boolean getStatus() {
            return status;
        }

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

    }

    public class AvgHashrate {

        @SerializedName("h1")
        @Expose
        private Double h1;
        @SerializedName("h3")
        @Expose
        private Double h3;
        @SerializedName("h6")
        @Expose
        private Double h6;
        @SerializedName("h12")
        @Expose
        private Double h12;
        @SerializedName("h24")
        @Expose
        private Double h24;

        public String getH1() {
            return getRoundEarnings(h1);
        }

        public String getH3() {
            return getRoundEarnings(h3);
        }

        public String getH6() {
            return getRoundEarnings(h6);
        }
        public String getH6unRound() {
            return h6.toString();
        }

        public String getH12() {
            return getRoundEarnings(h12);
        }

        public String getH24() {
            return getRoundEarnings(h24);
        }
    }

    public class Data {

        @SerializedName("account")
        @Expose
        private String account;
        @SerializedName("unconfirmed_balance")
        @Expose
        private Double unconfirmedBalance;
        @SerializedName("balance")
        @Expose
        private Double balance;
        @SerializedName("hashrate")
        @Expose
        private Double hashrate;
        @SerializedName("avgHashrate")
        @Expose
        private AvgHashrate avgHashrate;
        @SerializedName("workers")
        @Expose
        private List<Worker> workers = null;

        public String getAccount() {
            return account;
        }


        public String getUnconfirmedBalance() {
            return getRoundEarnings(unconfirmedBalance);
        }


        public String getBalance() {
            return getRoundEarnings(balance);
        }


        public String getHashrate() {
            return getRoundEarnings(hashrate);
        }


        public AvgHashrate getAvgHashrate() {
            return avgHashrate;
        }


        public List<Worker> getWorkers() {
            return workers;
        }


    }

    public class Worker extends AvgHashrate {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("uid")
        @Expose
        private Integer uid;
        @SerializedName("hashrate")
        @Expose
        private Double hashrate;
        @SerializedName("lastshare")
        @Expose
        private Integer lastshare;
        @SerializedName("rating")
        @Expose
        private Integer rating;


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Integer getUid() {
            return uid;
        }


        public String getHashrate() {
            return getRoundEarnings(hashrate);
        }


        public Integer getLastshare() {
            return lastshare;
        }


        public Integer getRating() {
            return rating;
        }


    }
}