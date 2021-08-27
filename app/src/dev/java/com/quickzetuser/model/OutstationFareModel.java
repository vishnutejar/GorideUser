package com.quickzetuser.model;

public class OutstationFareModel {


    private VehicleTypeModel vehicle;
    private FareData onewayfare;
    private FareData roundtripfare;

    public VehicleTypeModel getVehicle() {
        return vehicle;
    }

    public void setVehicle(VehicleTypeModel vehicle) {
        this.vehicle = vehicle;
    }

    public FareData getOnewayfare() {
        return onewayfare;
    }

    public void setOnewayfare(FareData onewayfare) {
        this.onewayfare = onewayfare;
    }

    public FareData getRoundtripfare() {
        return roundtripfare;
    }

    public void setRoundtripfare(FareData roundtripfare) {
        this.roundtripfare = roundtripfare;
    }

    @Override
    public String toString() {
        return getVehicle().getName();
    }


    public static class FareData extends FareModel {
        private float base_km;
        private float extra_km;
        private float driver_allounce;
        private float night_allounce;
        private PromoCodeModel promocode;
        private TotalTaxModel total_tax;

        public PromoCodeModel getPromocode() {
            return promocode;
        }

        public void setPromocode(PromoCodeModel promocode) {
            this.promocode = promocode;
        }

        public TotalTaxModel getTotal_tax() {
            return total_tax;
        }

        public void setTotal_tax(TotalTaxModel total_tax) {
            this.total_tax = total_tax;
        }

        public float getBase_km() {
            return base_km;
        }

        public String getBaseKmText() {
            float total_distance = getBase_km() * 1000;
            return getValidDistanceText(total_distance);
        }


        public void setBase_km(float base_km) {
            this.base_km = base_km;
        }

        public float getExtra_km() {
            return extra_km;
        }

        public String getExtraKmText() {
            float total_distance = getExtra_km() * 1000;
            return getValidDistanceText(total_distance);
        }


        public void setExtra_km(float extra_km) {
            this.extra_km = extra_km;
        }

        public float getDriver_allounce() {
            return driver_allounce;
        }

        public String getDriver_allounceText() {
            return getValidDecimalFormat(getDriver_allounce());
        }


        public void setDriver_allounce(float driver_allounce) {
            this.driver_allounce = driver_allounce;
        }

        public float getNight_allounce() {
            return night_allounce;
        }

        public String getNight_allounceText() {
            return getValidDecimalFormat(getNight_allounce());
        }

        public void setNight_allounce(float night_allounce) {
            this.night_allounce = night_allounce;
        }

    }
}
