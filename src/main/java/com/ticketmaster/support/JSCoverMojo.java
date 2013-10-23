package com.ticketmaster.support;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.IOException;

import jscover.filesystem.ConfigurationForFS;
import jscover.filesystem.FileSystemInstrumenter;
import jscover.report.ConfigurationForReport;
import jscover.report.Main;
import jscover.report.ReportFormat;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "JScoverage", defaultPhase = LifecyclePhase.VERIFY, threadSafe = true)
public class JSCoverMojo extends AbstractMojo {
    /**
     * Source Library of code
     */
	@Parameter(required = true)
	private File root;	
	
	/**
     * Location Report will be stored at
     */
	@Parameter
    private File dest;
	
	/**
     * Currently unused
     */
	@Parameter
	private String includes;
	
	/**
     * Excludes uses the No instrument Reg method Researching if using one parameter is possible
     */
	@Parameter
	private String excludes;
	
	@Parameter
	private String excludes2;
    
	/**
     * For us will always be COBERTURAXML, other formats are available but not supported right now
     */
	@Parameter
    private String reportFormat;
    

    public void execute()
        throws MojoExecutionException
    {
    	FileSystemInstrumenter runner = new FileSystemInstrumenter(); //used to actually run the coverage
        ConfigurationForFS configFS = new ConfigurationForFS(); // object used to configure the runner
        
        ReportFormat format = ReportFormat.valueOf(reportFormat);    //used to dynamically select report format ONLY SUPPORTS COBERTURAXML currently
    	ConfigurationForReport reportConfig = new ConfigurationForReport();  //passes report configurations up
    	
        
        configFS.setSrcDir(root);	//set Source Directory
        configFS.setDestDir(dest);  //set report directory
        configFS.addNoInstrumentReg(excludes); //excludes libs
        configFS.addNoInstrumentReg(excludes2);//excludes tests
                        
        runner.run(configFS); //start up JSCover (current failing at finding Rhino library need to find a way to introduce to implementing project other Rhino versions do not work with jscover)
        
        reportConfig.setReportFormat(format); //sets report format
        reportConfig.setSourceDirectory(root); //sets directory to run code against (I think)
                
        Main cover = new Main(); //used to print out report and pass to jenkins
        cover.setConfig(reportConfig); //passes configuration properties to the main object
        try {
			cover.saveCoberturaXml(); //saves a cobertura report
		} catch (IOException e) {
			
			e.printStackTrace();
		}

        
    }
}
