@Grapes([
	@Grab(group='joda-time', module='joda-time', version='2.1'),
	@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7' )])
import groovyx.net.http.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import org.joda.time.*
import org.joda.time.format.*

DateTimeFormatter inputfmt = ISODateTimeFormat.dateTimeNoMillis().withZone(DateTimeZone.UTC) 

def http = new HTTPBuilder( 'https://api.github.com/' )
def header_accept = 'application/json' 
def codice_repos = ''

http.request(GET,JSON) { req ->
  uri.path = 'orgs/codice/repos'
  uri.query = [type: 'source']
  headers.'Accept' = header_accept
  // Requires a user-agent
  headers.'User-Agent' = 'Mozilla/5.0 Ubuntu/8.10 Firefox/3.0.4'
 
  response.success = { resp, json ->
    assert resp.status == 200
    repoList = json
  }
}

println "Found ${repoList.size} Codice repos."
def ddfRepos = 0
repoList.each {
	def repoName = it.name
	def url = it.clone_url
	if (repoName.startsWith('ddf')) {
		println "Cloning repo: ${repoName} at ${url}"
		"git clone ${url}".execute()	
	    ddfRepos++
	}
}
println "Cloned ${ddfRepos} DDF repos."
