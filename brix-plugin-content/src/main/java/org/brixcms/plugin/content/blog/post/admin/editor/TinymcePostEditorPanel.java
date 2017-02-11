package org.brixcms.plugin.content.blog.post.admin.editor;

import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import wicket.contrib.tinymce.TinyMceBehavior;
import wicket.contrib.tinymce.image.ImageUploadPanel;
import wicket.contrib.tinymce.settings.Button;
import wicket.contrib.tinymce.settings.ContextMenuPlugin;
import wicket.contrib.tinymce.settings.DateTimePlugin;
import wicket.contrib.tinymce.settings.DirectionalityPlugin;
import wicket.contrib.tinymce.settings.EmotionsPlugin;
import wicket.contrib.tinymce.settings.FullScreenPlugin;
import wicket.contrib.tinymce.settings.IESpellPlugin;
import wicket.contrib.tinymce.settings.ImageUploadPlugin;
import wicket.contrib.tinymce.settings.MediaPlugin;
import wicket.contrib.tinymce.settings.PastePlugin;
import wicket.contrib.tinymce.settings.PreviewPlugin;
import wicket.contrib.tinymce.settings.PrintPlugin;
import wicket.contrib.tinymce.settings.SavePlugin;
import wicket.contrib.tinymce.settings.SearchReplacePlugin;
import wicket.contrib.tinymce.settings.SpellCheckPlugin;
import wicket.contrib.tinymce.settings.TablePlugin;
import wicket.contrib.tinymce.settings.TinyMCESettings;

/**
 * @author dan.simko@gmail.com
 */
// TODO upgrade to tinymce 4.x
@SuppressWarnings("serial")
public class TinymcePostEditorPanel extends Panel {

	public TinymcePostEditorPanel(String id, final IModel<String> markup) {
		super(id, markup);

		TinyMCESettings settings = new TinyMCESettings(TinyMCESettings.Theme.advanced);

		ContextMenuPlugin contextMenuPlugin = new ContextMenuPlugin();
		settings.register(contextMenuPlugin);

		// first toolbar
		SavePlugin savePlugin = new SavePlugin();
		settings.add(savePlugin.getSaveButton(), TinyMCESettings.Toolbar.first, TinyMCESettings.Position.before);
		settings.add(Button.newdocument, TinyMCESettings.Toolbar.first, TinyMCESettings.Position.before);
		settings.add(Button.separator, TinyMCESettings.Toolbar.first, TinyMCESettings.Position.before);
		settings.add(Button.fontselect, TinyMCESettings.Toolbar.first, TinyMCESettings.Position.after);
		settings.add(Button.fontsizeselect, TinyMCESettings.Toolbar.first, TinyMCESettings.Position.after);

		// second toolbar
		PastePlugin pastePlugin = new PastePlugin();
		SearchReplacePlugin searchReplacePlugin = new SearchReplacePlugin();
		DateTimePlugin dateTimePlugin = new DateTimePlugin();
		dateTimePlugin.setDateFormat("Date: %m-%d-%Y");
		dateTimePlugin.setTimeFormat("Time: %H:%M");
		PreviewPlugin previewPlugin = new PreviewPlugin();
		settings.add(Button.cut, TinyMCESettings.Toolbar.second, TinyMCESettings.Position.before);
		settings.add(Button.copy, TinyMCESettings.Toolbar.second, TinyMCESettings.Position.before);
		settings.add(pastePlugin.getPasteButton(), TinyMCESettings.Toolbar.second, TinyMCESettings.Position.before);
		settings.add(pastePlugin.getPasteTextButton(), TinyMCESettings.Toolbar.second, TinyMCESettings.Position.before);
		settings.add(pastePlugin.getPasteWordButton(), TinyMCESettings.Toolbar.second, TinyMCESettings.Position.before);
		settings.add(Button.separator, TinyMCESettings.Toolbar.second, TinyMCESettings.Position.before);
		settings.add(searchReplacePlugin.getSearchButton(), TinyMCESettings.Toolbar.second,
				TinyMCESettings.Position.before);
		settings.add(searchReplacePlugin.getReplaceButton(), TinyMCESettings.Toolbar.second,
				TinyMCESettings.Position.before);
		settings.add(Button.separator, TinyMCESettings.Toolbar.second, TinyMCESettings.Position.before);
		settings.add(Button.separator, TinyMCESettings.Toolbar.second, TinyMCESettings.Position.after);
		settings.add(dateTimePlugin.getDateButton(), TinyMCESettings.Toolbar.second, TinyMCESettings.Position.after);
		settings.add(dateTimePlugin.getTimeButton(), TinyMCESettings.Toolbar.second, TinyMCESettings.Position.after);
		settings.add(Button.separator, TinyMCESettings.Toolbar.second, TinyMCESettings.Position.after);
		settings.add(previewPlugin.getPreviewButton(), TinyMCESettings.Toolbar.second, TinyMCESettings.Position.after);
		settings.add(Button.separator, TinyMCESettings.Toolbar.second, TinyMCESettings.Position.after);
		settings.add(Button.forecolor, TinyMCESettings.Toolbar.second, TinyMCESettings.Position.after);
		settings.add(Button.backcolor, TinyMCESettings.Toolbar.second, TinyMCESettings.Position.after);

		// third toolbar
		TablePlugin tablePlugin = new TablePlugin();
		EmotionsPlugin emotionsPlugin = new EmotionsPlugin();
		IESpellPlugin iespellPlugin = new IESpellPlugin();
		MediaPlugin mediaPlugin = new MediaPlugin();
		PrintPlugin printPlugin = new PrintPlugin();
		FullScreenPlugin fullScreenPlugin = new FullScreenPlugin();
		DirectionalityPlugin directionalityPlugin = new DirectionalityPlugin();
		settings.add(tablePlugin.getTableControls(), TinyMCESettings.Toolbar.third, TinyMCESettings.Position.before);
		settings.add(emotionsPlugin.getEmotionsButton(), TinyMCESettings.Toolbar.third, TinyMCESettings.Position.after);
		settings.add(iespellPlugin.getIespellButton(), TinyMCESettings.Toolbar.third, TinyMCESettings.Position.after);
		settings.add(mediaPlugin.getMediaButton(), TinyMCESettings.Toolbar.third, TinyMCESettings.Position.after);
		settings.add(Button.separator, TinyMCESettings.Toolbar.third, TinyMCESettings.Position.after);
		settings.add(printPlugin.getPrintButton(), TinyMCESettings.Toolbar.third, TinyMCESettings.Position.after);
		settings.add(Button.separator, TinyMCESettings.Toolbar.third, TinyMCESettings.Position.after);
		settings
				.add(directionalityPlugin.getLtrButton(), TinyMCESettings.Toolbar.third, TinyMCESettings.Position.after);
		settings
				.add(directionalityPlugin.getRtlButton(), TinyMCESettings.Toolbar.third, TinyMCESettings.Position.after);
		settings.add(Button.separator, TinyMCESettings.Toolbar.third, TinyMCESettings.Position.after);
		settings.add(fullScreenPlugin.getFullscreenButton(), TinyMCESettings.Toolbar.third,
				TinyMCESettings.Position.after);

		// fourth toolbar
		SpellCheckPlugin spellCheckPlugin = new SpellCheckPlugin();
		settings.add(spellCheckPlugin.getSpellCheckButton(), TinyMCESettings.Toolbar.fourth,
				TinyMCESettings.Position.after);

		// other settings
		settings.setToolbarAlign(TinyMCESettings.Align.left);
		settings.setToolbarLocation(TinyMCESettings.Location.top);
		settings.setStatusbarLocation(TinyMCESettings.Location.bottom);
		settings.setResizing(true);

		ImageUploadPanel imageUploadPanel = new ImageUploadPanel("uploadPanel");
		ImageUploadPlugin plugin = new ImageUploadPlugin(imageUploadPanel.getImageUploadBehavior());
		settings.add(plugin.getImageUploadButton(), TinyMCESettings.Toolbar.first, TinyMCESettings.Position.after);
		add(imageUploadPanel);

		TextArea<String> textArea = new TextArea<String>("content", markup);
		textArea.add(new TinyMceBehavior(settings));
		add(textArea);

	}
}