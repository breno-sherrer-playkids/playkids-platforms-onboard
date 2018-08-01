<template>
  <div class="row-fluid">
    <div class="col-sm-4 col-sm-offset-4">
      <div class="panel panel-default">
        <div class="panel-heading">
          <h3 class="panel-title text-center">Select the amount of Credits</h3>
        </div>
        
        <div class="panel-body">
          <el-form :model="model" :rules="rules" ref="form">

              <el-form-item label="Quantity" prop="quantity">
                <el-input type="number" name="quantity" id="quantity" value="" v-model="model.quantity" />
              </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="submit('form')">Buy Credits</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
function buyCredits() {
  this.$userService
    .buyCredits(this.model.quantity)
    .then(response => {
      this.$message.success(
        "Congratulations! You bought " + this.model.quantity + " credits."
      );

      // TODO: create some kind of get function to see the funds
      this.$router.push("/");
    })
    .catch(error => {
      if (error.status === 400) {
        let prettyError = error.body
          .map(e => {
            return e.description;
          })
          .join("<br />");

        this.$message.error({
          dangerouslyUseHTMLString: true,
          message: "<b>Failed to buy credits</b>: <br />" + prettyError,
          duration: 6000
        });
      } else if (error.status !== 401) {
        this.$message.error("Unknown error: " + error);
      }
    });
}

export default {
  name: "Credits",
  data() {
    return {
      model: {
        quantity: 10.0
      },
      rules: {
        quantity: [
          { required: true, message: "Quantity is mandatory", trigger: "blur" }
        ]
      }
    };
  },
  methods: {
    submit(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          buyCredits.call(this);
        } else {
          this.$message.error("Invalid quantity");
        }
      });
    }
  }
};
</script>
