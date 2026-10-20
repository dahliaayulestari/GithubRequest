package dahlia.com.example.githubrequest

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dahlia.com.example.githubrequest.databinding.ActivityMainBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val client = OkHttpClient()
    private val request = OkHttpRequest(client)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGetInfo.setOnClickListener {
            val loginID = binding.tvSearch.text.toString().trim()

            if (loginID.isEmpty()) {
                Toast.makeText(
                    this,
                    "Silakan masukkan username GitHub!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val url = "https://api.github.com/users/$loginID"
                fetchGitHubInfo(url)
            }
        }
    }

    private fun fetchGitHubInfo(strURL: String) {
        request.GET(strURL, object : Callback {

            override fun onResponse(call: Call, response: Response) {

                val responseData = response.body?.string()

                runOnUiThread {
                    try {

                        if (responseData == null) {
                            binding.tvUserInfo.text = "Data tidak tersedia"
                            return@runOnUiThread
                        }

                        val userObject = JSONObject(responseData)

                        if (!response.isSuccessful) {

                            val message = userObject.optString(
                                "message",
                                "Terjadi kesalahan"
                            )

                            binding.tvUserInfo.text =
                                "HTTP ${response.code}\n$message"

                            return@runOnUiThread
                        }

                        val id = userObject.optString("id", "-")
                        val name = userObject.optString("name", "-")
                        val username = userObject.optString("login", "-")
                        val url = userObject.optString("html_url", "-")
                        val blog = userObject.optString("blog", "-")
                        val bio = userObject.optString("bio", "-")
                        val company = userObject.optString("company", "-")
                        val location = userObject.optString("location", "-")
                        val followers = userObject.optString("followers", "0")
                        val following = userObject.optString("following", "0")

                        binding.tvUserInfo.text =
                            "ID: $id\n" +
                                    "Username: $username\n" +
                                    "Name: $name\n" +
                                    "URL: $url\n" +
                                    "Blog: $blog\n" +
                                    "Bio: $bio\n" +
                                    "Company: $company\n" +
                                    "Location: $location\n" +
                                    "Followers: $followers\n" +
                                    "Following: $following"

                    } catch (e: JSONException) {

                        e.printStackTrace()

                        binding.tvUserInfo.text =
                            "Gagal membaca data JSON:\n${e.message}"
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {

                runOnUiThread {
                    binding.tvUserInfo.text =
                        "Koneksi gagal:\n${e.message}"
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()

        binding.tvSearch.setText("")
        binding.tvSearch.hint = "Enter GitHub username"
    }
}