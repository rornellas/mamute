package org.mamute.controllers;

import javax.inject.Inject;

import org.mamute.brutauth.auth.rules.ModeratorOnlyRule;
import org.mamute.dao.TagDAO;
import org.mamute.dao.TagPageDAO;
import org.mamute.model.Tag;
import org.mamute.model.TagPage;
import org.mamute.validators.TagPageValidator;

import br.com.caelum.brutauth.auth.annotations.CustomBrutauthRules;
import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Result;

@Controller
public class TagPageController {

	@Inject private TagDAO tags;
	@Inject private Result result;
	@Inject private TagPageDAO tagPages;
	@Inject private TagPageValidator validator;

	@Get("/tag/{tagName}/sobre/novo")
	@CustomBrutauthRules(ModeratorOnlyRule.class)
	public void tagPageForm(String tagName){
		if(validator.validateCreationWithTag(tagName)){
			result.include("tag", tags.findByName(tagName));
		}
	}

	@Get("/tag/{tagName}/sobre/editar")
	@CustomBrutauthRules(ModeratorOnlyRule.class)
	public void editTagPageForm(String tagName){
		TagPage tagPage = tagPages.findByTag(tagName);
		result.include("tagPage", tagPage);
	}
	
	@Post("/tag/{tagName}/sobre/editar")
	@CustomBrutauthRules(ModeratorOnlyRule.class)
	public void editTagPage(String tagName, String about){
		TagPage tagPage = tagPages.findByTag(tagName);
		tagPage.setAbout(about);
		if(!validator.validate(tagPage)){
			validator.onErrorRedirectTo(TagPageController.class).editTagPageForm(tagPage.getTagName());
			return;
		}
		result.redirectTo(this).showTagPage(tagPage.getTagName());
	}
	
	@Post("/tag/{tagName}/sobre/novo")
	@CustomBrutauthRules(ModeratorOnlyRule.class)
	public void newTagPage(String tagName, String about){
		if(!validator.validateCreationWithTag(tagName)) return;
		Tag tag = tags.findByName(tagName);
		TagPage tagPage = new TagPage(tag, about);
		if(!validator.validate(tagPage)){
			validator.onErrorRedirectTo(TagPageController.class).tagPageForm(tagPage.getTagName());
			return;
		}
		tagPages.save(tagPage);
		result.redirectTo(this).showTagPage(tagPage.getTagName()); 
	}
	
	@Get("/tag/{tagName}/sobre")
	public void showTagPage(String tagName){
		TagPage tagPage = tagPages.findByTag(tagName);
		result.include(tagPage);
		result.include("hasAbout", tags.hasAbout(tagPage.getTag()));
	}
}