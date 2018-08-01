<template>
  <div id="auth" class="row-fluid">
    <div class="col-sm-4 col-sm-offset-4">
      <div class="panel panel-default">
        <div class="panel-heading">
          <h3 class="panel-title text-center">Fill out the form to create your account!</h3>
        </div>
        
        <div class="panel-body">
          <el-form class="form-signup" :model="auth" :rules="rules" ref="form">

              <el-form-item label="E-Mail" prop="email">
                <el-input type="text" name="email" id="email" value="" v-model="auth.email" />
              </el-form-item>

              <el-form-item label="Password" prop="password">
                <el-input type="password" name="password" id="password"  value="" v-model="auth.password" 
                @keyup.enter.native="submit('form')" />
              </el-form-item>

            <el-form-item>
              <el-button type="primary" name="login_button" @click="submit('form')">Create my account</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
function authenticate() {
  this.$authenticationService
    .signUp(this.auth.email, this.auth.password)
    .then(response => {
      this.$message.success("Account created, welcome to PlayKids Lottery!");
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
          message: "<b>Failed to create account</b>: <br />" + prettyError,
          duration: 6000
        });
      } else {
        this.$message.error(
          "Unknown error, please contact the support at breno.sherrer@playkids.com"
        );
      }
    });
}

export default {
  name: "auth",
  data() {
    return {
      auth: {
        email: "",
        password: ""
      },
      rules: {
        email: [
          { required: true, message: "E-mail is mandatory", trigger: "blur" }
        ],
        password: [
          { required: true, message: "Password is mandatory", trigger: "blur" }
        ]
      }
    };
  },
  methods: {
    submit(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          authenticate.call(this);
        } else {
          this.$message.error("Invalid field username and/or password");
        }
      });
    }
  }
};
</script>
<style lang="scss" scoped>
</style>
